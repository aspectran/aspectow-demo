package app.root.appmon.measurement;

import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;

import java.util.Timer;
import java.util.TimerTask;

public class Measuring extends AbstractLifeCycle {

    private static final int DEFAULT_SAMPLE_INTERVAL = 5000;

    private final MeasurementManager manager;

    private final MeasurementInfo info;

    private final String group;

    private final String name;

    private final int sampleInterval;

    private final MeasureCollector dataCollector;

    private Timer timer;

    public Measuring(@NonNull MeasurementManager manager,
                     @NonNull MeasurementInfo info,
                     MeasureCollector dataCollector) {
        this.manager = manager;
        this.info = info;
        this.group = info.getGroup();
        this.name = info.getName();
        this.sampleInterval = (info.getSampleInterval() > 0 ? info.getSampleInterval() : DEFAULT_SAMPLE_INTERVAL);
        this.dataCollector = dataCollector;
    }

    public MeasurementInfo getInfo() {
        return info;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public int getSampleInterval() {
        return sampleInterval;
    }

    private void broadcast() {
        String data = dataCollector.collect();
        if (data != null) {
            manager.broadcast(name, "stats:" + data);
        }
    }

    @Override
    protected synchronized void doStart() throws Exception {
        if (timer == null) {
            timer = new Timer("MeasuringTimer[interval=" + sampleInterval + "]");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcast();
                }
            }, 0, sampleInterval);
            dataCollector.init();
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
            return new ToStringBuilder(super.toString(), info).toString();
        } else {
            return super.toString();
        }
    }

}
