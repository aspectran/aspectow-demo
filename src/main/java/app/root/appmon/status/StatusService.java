package app.root.appmon.status;

import app.root.appmon.config.StatusInfo;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatusService extends AbstractLifeCycle {

    private static final String LABEL_STATUS = ":status:";

    private final StatusManager statusManager;

    private final StatusInfo statusInfo;

    private final StatusReader statusReader;

    private final String label;

    private final int sampleInterval;

    private Timer timer;

    public StatusService(@NonNull StatusManager statusManager,
                         @NonNull StatusInfo statusInfo,
                         @NonNull StatusReader statusReader) {
        this.statusManager = statusManager;
        this.statusInfo = statusInfo;
        this.statusReader = statusReader;
        this.label = statusInfo.getGroup() + ":" + statusInfo.getName() + LABEL_STATUS;
        this.sampleInterval = statusInfo.getSampleInterval();
    }

    public StatusInfo getStatusInfo() {
        return statusInfo;
    }

    void readStatus(@NonNull List<String> messages) {
        String data = statusReader.read();
        if (data != null) {
            messages.add(label + data);
        }
    }

    public void broadcast(String message) {
        statusManager.broadcast(label + message);
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
