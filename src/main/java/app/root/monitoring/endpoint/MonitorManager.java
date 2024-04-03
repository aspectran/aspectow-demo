package app.root.monitoring.endpoint;

import app.root.monitoring.logtail.LogTailConfig;
import app.root.monitoring.logtail.LogTailInfo;
import app.root.monitoring.logtail.LogTailer;
import app.root.monitoring.logtail.LogTailerManager;
import app.root.monitoring.measurement.MeasurementInfo;
import app.root.monitoring.measurement.Measuring;
import app.root.monitoring.measurement.MeasuringManager;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;
import jakarta.websocket.Session;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Created: 4/3/24</p>
 */
public class MonitorManager {

    private static final Logger logger = LoggerFactory.getLogger(MonitorManager.class);

    private static final String LOGTAIL_CONFIG_FILE = "app/root/logtail-config.apon";

    private static final String MONITOR_GROUPS_PROPERTY = "monitorGroups";

    private final MonitorEndpoint endpoint;

    private final LogTailerManager logTailerManager;

    private final MeasuringManager measuringManager;

    public MonitorManager(MonitorEndpoint endpoint) throws Exception {
        this.endpoint = endpoint;
        LogTailConfig logTailConfig = new LogTailConfig(ResourceUtils.getResourceAsReader(LOGTAIL_CONFIG_FILE));
        this.logTailerManager = createLogTailerManager(logTailConfig);
        this.measuringManager = createMeasuring(logTailConfig.getMeasurementInfoList());
    }

    public MonitorEndpoint getEndpoint() {
        return endpoint;
    }

    public ActivityContext getActivityContext() {
        return endpoint.getActivityContext();
    }

    public void broadcast(String message) {
        endpoint.broadcast(message);
    }

    private LogTailerManager createLogTailerManager(LogTailConfig logTailConfig) {
        LogTailerManager logTailerManager = new LogTailerManager(this);
        Map<String, LogTailer> tailers = new LinkedHashMap<>();
        List<LogTailInfo> tailerInfoList = logTailConfig.getLogTailInfoList();
        for (LogTailInfo logTailInfo : tailerInfoList) {
            File logFile = null;
            try {
                String file = endpoint.getActivityContext().getApplicationAdapter().toRealPath(logTailInfo.getFile());
                logFile = new File(file).getCanonicalFile();
            } catch (IOException e) {
                logger.error("Failed to resolve absolute path to log file " + logTailInfo.getFile(), e);
            }
            if (logFile != null) {
                LogTailer tailer = new LogTailer(logTailerManager, logTailInfo, logFile);
                logTailerManager.addLogTailer(logTailInfo.getName(), tailer);
            }
        }
        return logTailerManager;
    }

    private MeasuringManager createMeasuring(@NonNull List<MeasurementInfo> measurementInfoList) {
        MeasuringManager measuringManager = new MeasuringManager(this);
        for (MeasurementInfo measurementInfo : measurementInfoList) {
            Measuring measuring = new Measuring(measuringManager, measurementInfo);
            measuringManager.addMeasuring(measurementInfo.getName(), measuring);
        }
        return measuringManager;
    }

    public List<LogTailInfo> getLogTailInfoList() {
        return logTailerManager.getLogTailInfoList();
    }

    public synchronized void join(Session session, String[] groups) {
        String[] joinGroups = getJoinedGroups(session, groups);
        logTailerManager.join(joinGroups);
        measuringManager.join(joinGroups);
        if (joinGroups != null) {
            saveJoinedGroups(session, joinGroups);
        } else {
            removeJoinedGroups(session);
        }
    }

    public synchronized void release(Session session) {
        String[] joinGroups = getJoinedGroups(session, null);
        logTailerManager.release(joinGroups);
        measuringManager.release(joinGroups);
        removeJoinedGroups(session);
    }

    public boolean isUsingGroup(String group) {
        if (StringUtils.hasLength(group)) {
            for (Session session : endpoint.getSessions()) {
                String[] savedGroups = (String[]) session.getUserProperties().get(MONITOR_GROUPS_PROPERTY);
                if (savedGroups != null) {
                    for (String saved : savedGroups) {
                        if (group.equals(saved)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private String[] getJoinedGroups(Session session, String[] groups) {
        Set<String> joinGroups = new HashSet<>();
        String[] savedGroups = (String[])session.getUserProperties().get(MONITOR_GROUPS_PROPERTY);
        if (savedGroups != null) {
            Collections.addAll(joinGroups, savedGroups);
        }
        if (groups != null) {
            Collections.addAll(joinGroups, groups);
        }
        if (!joinGroups.isEmpty()) {
            return joinGroups.toArray(new String[0]);
        } else {
            return null;
        }
    }

    private void saveJoinedGroups(Session session, String[] joinGroups) {
        session.getUserProperties().put(MONITOR_GROUPS_PROPERTY, joinGroups);
    }

    private void removeJoinedGroups(Session session) {
        session.getUserProperties().remove(MONITOR_GROUPS_PROPERTY);
    }

}
