package app.root.appmon.service.event;

import app.root.appmon.config.EventInfo;
import app.root.appmon.manager.AppMonManager;
import com.aspectran.utils.ClassUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.List;

/**
 * <p>Created: 2024-12-18</p>
 */
public class EventServiceManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceManagerBuilder.class);

    @NonNull
    public static EventServiceManager build(@NonNull AppMonManager appMonManager,
                                            @NonNull String groupName,
                                            @NonNull List<EventInfo> eventInfoList) throws Exception {
        EventServiceManager eventServiceManager = new EventServiceManager(appMonManager, groupName);
        for (EventInfo eventInfo : eventInfoList) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create EventService", eventInfo));
            }
            eventInfo.validateRequiredParameters();

            EventReader eventReader = createEventReader(eventServiceManager, eventInfo);
            EventService eventService = new EventService(eventServiceManager, eventInfo, eventReader);
            eventServiceManager.addService(eventService);
            appMonManager.addServiceManager(eventServiceManager);
        }
        return eventServiceManager;
    }

    @NonNull
    private static EventReader createEventReader(
            @NonNull EventServiceManager eventServiceManager, @NonNull EventInfo eventInfo) throws Exception {
        try {
            Class<EventReader> receiverType = ClassUtils.classForName(eventInfo.getReader());
            Object[] args = { eventServiceManager, eventInfo };
            Class<?>[] argTypes = { EventServiceManager.class, EventInfo.class };
            return ClassUtils.createInstance(receiverType, args, argTypes);
        } catch (Exception e) {
            throw new Exception("Failed to create event receiver: " + eventInfo, e);
        }
    }
    
}
