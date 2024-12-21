package app.root.appmon.exporter.event.request;

import app.root.appmon.config.EventInfo;
import app.root.appmon.exporter.event.EventExporter;
import app.root.appmon.exporter.event.EventExporterManager;
import app.root.appmon.exporter.event.EventReader;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.component.aspect.AspectRuleRegistry;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.JoinpointRule;
import com.aspectran.core.context.rule.type.AspectAdviceType;
import com.aspectran.core.context.rule.type.JoinpointTargetType;
import com.aspectran.core.service.CoreServiceHolder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

/**
 * <p>Created: 2024-12-18</p>
 */
public class RequestEventReader implements EventReader {

    private static final Logger logger = LoggerFactory.getLogger(RequestEventReader.class);

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

    public EventExporter getEventService() {
        return eventExporterManager.getExporter(eventInfo.getName());
    }

    @Override
    public void start() throws Exception {
        AspectRuleRegistry aspectRuleRegistry = findAspectRuleRegistry(target);

        AspectRule aspectRule = new AspectRule();
        aspectRule.setId(aspectId);
        aspectRule.setOrder(-1);
        aspectRule.setIsolated(true);

        JoinpointRule joinpointRule = new JoinpointRule();
        joinpointRule.setJoinpointTargetType(JoinpointTargetType.ACTIVITY);
        aspectRule.setJoinpointRule(joinpointRule);

        AspectAdviceRule beforeAspectAdviceRule = aspectRule.newAspectAdviceRule(AspectAdviceType.BEFORE);
        beforeAspectAdviceRule.setAdviceAction(activity -> {
            RequestEventAdvice requestEventAspect = new RequestEventAdvice(getEventService());
            requestEventAspect.request(activity);
            return requestEventAspect;
        });

        AspectAdviceRule afterAspectAdviceRule = aspectRule.newAspectAdviceRule(AspectAdviceType.AFTER);
        afterAspectAdviceRule.setAdviceAction(new Executable() {
            @Override
            public Object execute(Activity activity) throws Exception {
                RequestEventAdvice requestEventAspect = activity.getBeforeAdviceResult(aspectId);
                requestEventAspect.complete(activity);
                return null;
            }

        });

        aspectRuleRegistry.addAspectRule(aspectRule);
    }

    @Override
    public void stop() {
        try {
            AspectRuleRegistry aspectRuleRegistry = findAspectRuleRegistry(target);
            aspectRuleRegistry.removeAspectRule(aspectId);
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    @NonNull
    private AspectRuleRegistry findAspectRuleRegistry(String contextName) throws Exception {
        ActivityContext context = CoreServiceHolder.findActivityContext(contextName);
        if (context == null) {
            throw new Exception("Could not find ActivityContext with name '" + target + "'");
        }
        return context.getAspectRuleRegistry();
    }

}
