package app.root.appmon.status;

import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatusService extends AbstractLifeCycle {

    private static final int DEFAULT_SAMPLE_INTERVAL = 5000;

    private static final String LABEL_STATUS = ":status:";

    private final StatusManager manager;

    private final StatusInfo info;

    private final String group;

    private final String name;

    private final StatusReader statusReader;

    private final String label;

    private final int sampleInterval;

    private Timer timer;

    public StatusService(@NonNull StatusManager manager,
                         @NonNull StatusInfo info,
                         StatusReader statusReader) {
        this.manager = manager;
        this.info = info;
        this.group = info.getGroup();
        this.name = info.getName();
        this.statusReader = statusReader;
        this.label = this.name + LABEL_STATUS + info.getLabel() + ":";
        this.sampleInterval = (info.getSampleInterval() > 0 ? info.getSampleInterval() : DEFAULT_SAMPLE_INTERVAL);
    }

    public StatusInfo getInfo() {
        return info;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public void refresh() {
        statusReader.init();
    }

    void readStatus(@NonNull List<String> messages) {
        String data = statusReader.read();
        if (data != null) {
            messages.add(label + data);
        }
    }

    private void broadcast() {
        String data = statusReader.readIfChanged();
        if (data != null) {
            manager.broadcast(label + data);
        }
    }

    @Override
    protected synchronized void doStart() throws Exception {
        if (timer == null) {
            String name = new ToStringBuilder("StatusReadingTimer")
                    .append("statusReader", statusReader)
                    .append("sampleInterval", sampleInterval)
                    .toString();
            timer = new Timer(name);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcast();
                }
            }, 0, sampleInterval);
            refresh();
            broadcast();
        }
    }

    @Override
    protected synchronized void doStop() throws Exception {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public String toString() {
        if (isStopped()) {
            return ToStringBuilder.toString(super.toString(), info);
        } else {
            return super.toString();
        }
    }

}
