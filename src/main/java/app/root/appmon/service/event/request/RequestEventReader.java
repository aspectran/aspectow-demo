package app.root.appmon.service.event.request;

import app.root.appmon.config.EventInfo;
import app.root.appmon.service.event.EventReader;
import app.root.appmon.service.event.EventService;
import app.root.appmon.service.event.EventServiceManager;
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

    private final static String ASPECT_ID = "requestEventReaderAspect";

    private final EventServiceManager eventServiceManager;

    private final EventInfo eventInfo;

    private final String target;

    public RequestEventReader(@NonNull EventServiceManager eventServiceManager, @NonNull EventInfo eventInfo) {
        this.eventServiceManager = eventServiceManager;
        this.eventInfo = eventInfo;
        this.target = eventInfo.getTarget();
    }

    public EventService getEventService() {
        return eventServiceManager.getService(eventInfo.getName());
    }

    @Override
    public void start() throws Exception {
        AspectRuleRegistry aspectRuleRegistry = findAspectRuleRegistry(target);

        AspectRule aspectRule = new AspectRule();
        aspectRule.setId(ASPECT_ID);
        aspectRule.setOrder(-1);
        aspectRule.setIsolated(true);

        JoinpointRule joinpointRule = new JoinpointRule();
        joinpointRule.setJoinpointTargetType(JoinpointTargetType.ACTIVITY);
        aspectRule.setJoinpointRule(joinpointRule);

        AspectAdviceRule beforeAspectAdviceRule = aspectRule.newAspectAdviceRule(AspectAdviceType.BEFORE);
        beforeAspectAdviceRule.setAdviceAction(activity -> {
            RequestEventAspect requestEventAspect = new RequestEventAspect(getEventService());
            requestEventAspect.request(activity);
            return requestEventAspect;
        });

        AspectAdviceRule afterAspectAdviceRule = aspectRule.newAspectAdviceRule(AspectAdviceType.AFTER);
        afterAspectAdviceRule.setAdviceAction(activity -> {
            RequestEventAspect requestEventAspect = activity.getBeforeAdviceResult(ASPECT_ID);
            requestEventAspect.complete(activity);
            return null;
        });

        aspectRuleRegistry.addAspectRule(aspectRule);
    }

    @Override
    public void stop() {
        try {
            AspectRuleRegistry aspectRuleRegistry = findAspectRuleRegistry(target);
            aspectRuleRegistry.removeAspectRule(ASPECT_ID);
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
