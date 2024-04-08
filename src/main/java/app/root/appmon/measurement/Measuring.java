package app.root.appmon.measurement;

import app.root.appmon.measurement.session.SessionStatsPayload;
import app.root.appmon.measurement.session.TowSessionStats;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Measuring extends AbstractLifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(Measuring.class);

    private static final int DEFAULT_SAMPLE_INTERVAL = 5000;

    private final MeasurementManager manager;

    private final MeasurementInfo info;

    private final String group;

    private final String name;

    private final int sampleInterval;

    private final TowSessionStats towSessionStats;

    private Timer timer;

    public Measuring(@NonNull MeasurementManager manager, @NonNull MeasurementInfo info) {
        this.manager = manager;
        this.info = info;
        this.group = info.getGroup();
        this.name = info.getName();
        this.sampleInterval = (info.getSampleInterval() > 0 ? info.getSampleInterval() : DEFAULT_SAMPLE_INTERVAL);
        this.towSessionStats = new TowSessionStats(manager.getBean("tow.server"), info.getName());
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

    private void broadcastStats() {
        SessionStatsPayload stats = towSessionStats.getSessionStatsPayload();
        if (stats != null) {
            try {
                manager.broadcast(name, "stats:" + stats.toJson());
            } catch (IOException e) {
                logger.warn(e);
            }
        }
    }

    @Override
    protected synchronized void doStart() throws Exception {
        if (timer == null) {
            timer = new Timer("MeasuringTimer[interval=" + sampleInterval + "]");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcastStats();
                }
            }, 0, sampleInterval);
            towSessionStats.clear();
            broadcastStats();
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
