package app.root.appmon.service.status;

import app.root.appmon.config.StatusInfo;
import app.root.appmon.service.Service;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.Parameters;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatusService extends Service {

    private static final String LABEL_STATUS = ":status:";

    private final StatusServiceManager statusServiceManager;

    private final StatusInfo statusInfo;

    private final StatusReader statusReader;

    private final String label;

    private final int sampleInterval;

    private Timer timer;

    public StatusService(@NonNull StatusServiceManager statusServiceManager,
                         @NonNull StatusInfo statusInfo,
                         @NonNull StatusReader statusReader) {
        this.statusServiceManager = statusServiceManager;
        this.statusInfo = statusInfo;
        this.statusReader = statusReader;
        this.label = statusInfo.getGroup() + ":" + statusInfo.getName() + LABEL_STATUS;
        this.sampleInterval = statusInfo.getSampleInterval();
    }

    @Override
    public String getName() {
        return statusInfo.getName();
    }

    @SuppressWarnings("unchecked")
    public <V extends Parameters> V getServiceInfo() {
        return (V)statusInfo;
    }

    @Override
    public void read(@NonNull List<String> messages) {
        String data = statusReader.read();
        if (data != null) {
            messages.add(label + data);
        }
    }

    @Override
    public void broadcast(String message) {
        statusServiceManager.broadcast(label + message);
    }

    private void broadcastIfChanged() {
        String data = statusReader.readIfChanged();
        if (data != null) {
            broadcast(data);
        }
    }

    @Override
    protected void doStart() throws Exception {
        statusReader.start();
        broadcastIfChanged();
        if (sampleInterval > 0 && timer == null) {
            String name = new ToStringBuilder("StatusReadingTimer")
                    .append("statusReader", statusReader)
                    .append("sampleInterval", sampleInterval)
                    .toString();
            timer = new Timer(name);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcastIfChanged();
                }
            }, 0, sampleInterval);
        }
    }

    @Override
    protected void doStop() throws Exception {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        statusReader.stop();
    }

    @Override
    public String toString() {
        if (isStopped()) {
            return ToStringBuilder.toString(super.toString(), statusInfo);
        } else {
            return super.toString();
        }
    }

}
