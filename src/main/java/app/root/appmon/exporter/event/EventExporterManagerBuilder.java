package app.root.appmon.exporter.event;

import app.root.appmon.config.EventInfo;
import com.aspectran.utils.ClassUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.List;

/**
 * <p>Created: 2024-12-18</p>
 */
public class EventExporterManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EventExporterManagerBuilder.class);

    @NonNull
    public static void build(@NonNull EventExporterManager eventExporterManager,
                             @NonNull List<EventInfo> eventInfoList) throws Exception {
        for (EventInfo eventInfo : eventInfoList) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create EventExporter", eventInfo));
            }

            eventInfo.validateRequiredParameters();

            EventReader eventReader = createEventReader(eventExporterManager, eventInfo);
            EventExporter eventExporter = new EventExporter(eventExporterManager, eventInfo, eventReader);
            eventExporterManager.addExporter(eventExporter);
        }
    }

    @NonNull
    private static EventReader createEventReader(
            @NonNull EventExporterManager eventExporterManager, @NonNull EventInfo eventInfo) throws Exception {
        try {
            Class<EventReader> readerType = ClassUtils.classForName(eventInfo.getReader());
            Object[] args = { eventExporterManager, eventInfo };
            Class<?>[] argTypes = { EventExporterManager.class, EventInfo.class };
            return ClassUtils.createInstance(readerType, args, argTypes);
        } catch (Exception e) {
            throw new Exception("Failed to create event reader: " + eventInfo, e);
        }
    }
    
}
