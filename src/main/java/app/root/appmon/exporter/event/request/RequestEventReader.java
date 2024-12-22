package app.root.appmon.exporter.event.request;

import app.root.appmon.config.EventInfo;
import app.root.appmon.exporter.event.EventExporter;
import app.root.appmon.exporter.event.EventExporterManager;
import app.root.appmon.exporter.event.EventReader;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.JoinpointRule;
import com.aspectran.core.context.rule.type.AspectAdviceType;
import com.aspectran.core.context.rule.type.JoinpointTargetType;
import com.aspectran.core.service.CoreServiceHolder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.json.JsonBuilder;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>Created: 2024-12-18</p>
 */
public class RequestEventReader implements EventReader {

    private static final Logger logger = LoggerFactory.getLogger(RequestEventReader.class);

    private final AtomicLong counter = new AtomicLong();

    private final EventExporterManager eventExporterManager;

    private final EventInfo eventInfo;

    private final String target;

    private final String aspectId;

    public RequestEventReader(@NonNull EventExporterManager eventExporterManager, @NonNull EventInfo eventInfo) {
        this.eventExporterManager = eventExporterManager;
        this.eventInfo = eventInfo;
        this.target = eventInfo.getTarget();
        this.aspectId = getClass().getName() + ".ASPECT-" + hashCode();
    }

    public EventExporter getEventExporter() {
        return eventExporterManager.getExporter(eventInfo.getName());
    }

    @Override
    public void start() throws Exception {
        ActivityContext context = CoreServiceHolder.findActivityContext(target);
        if (context == null) {
            throw new Exception("Could not find ActivityContext named '" + target + "'");
        }

        AspectRule aspectRule = new AspectRule();
        aspectRule.setId(aspectId);
        aspectRule.setOrder(-1);
        aspectRule.setIsolated(true);

        JoinpointRule joinpointRule = new JoinpointRule();
        joinpointRule.setJoinpointTargetType(JoinpointTargetType.ACTIVITY);
        aspectRule.setJoinpointRule(joinpointRule);

        AspectAdviceRule beforeAspectAdviceRule = aspectRule.newAspectAdviceRule(AspectAdviceType.BEFORE);
        beforeAspectAdviceRule.setAdviceAction(activity -> {
            long number = counter.incrementAndGet();
            RequestEventAdvice requestEventAspect = new RequestEventAdvice(number);
            requestEventAspect.request(activity);
            return requestEventAspect;
        });

        AspectAdviceRule afterAspectAdviceRule = aspectRule.newAspectAdviceRule(AspectAdviceType.AFTER);
        afterAspectAdviceRule.setAdviceAction(activity -> {
            RequestEventAdvice requestEventAspect = activity.getBeforeAdviceResult(aspectId);
            String json = requestEventAspect.complete(activity);
            getEventExporter().broadcast(json);
            return null;
        });

        context.getAspectRuleRegistry().addAspectRule(aspectRule);
    }

    @Override
    public void stop() {
        try {
            ActivityContext context = CoreServiceHolder.findActivityContext(target);
            if (context != null) {
                context.getAspectRuleRegistry().removeAspectRule(aspectId);
            }
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    @Override
    public String read() {
        return new JsonBuilder()
                .prettyPrint(false)
                .nullWritable(false)
                .object()
                    .put("number", counter.longValue())
                .endObject()
                .toString();
    }

}
