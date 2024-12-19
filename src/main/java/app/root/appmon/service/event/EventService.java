package app.root.appmon.service.event;

import app.root.appmon.config.EventInfo;
import app.root.appmon.service.Service;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.Parameters;

import java.util.List;
import java.util.Timer;

/**
 * <p>Created: 2024-12-18</p>
 */
public class EventService extends Service {

    private static final String LABEL_EVENT = ":event:";

    private final EventServiceManager eventServiceManager;

    private final EventInfo eventInfo;

    private final EventReader eventReader;

    private final String label;

    private Timer timer;

    public EventService(@NonNull EventServiceManager eventServiceManager,
                        @NonNull EventInfo eventInfo,
                        @NonNull EventReader eventReader) {
        this.eventServiceManager = eventServiceManager;
        this.eventInfo = eventInfo;
        this.eventReader = eventReader;
        this.label = eventInfo.getGroup() + ":" + eventInfo.getName() + LABEL_EVENT;
    }

    @Override
    public String getName() {
        return eventInfo.getName();
    }

    @SuppressWarnings("unchecked")
    public <V extends Parameters> V getServiceInfo() {
        return (V)eventInfo;
    }

    @Override
    public void read(@NonNull List<String> messages) {
    }

    @Override
    public void broadcast(String message) {
        eventServiceManager.broadcast(label + message);
    }

    @Override
    protected void doStart() throws Exception {
        eventReader.start();
    }

    @Override
    protected void doStop() throws Exception {
        eventReader.stop();
    }

    @Override
    public String toString() {
        if (isStopped()) {
            return ToStringBuilder.toString(super.toString(), eventInfo);
        } else {
            return super.toString();
        }
    }

}
