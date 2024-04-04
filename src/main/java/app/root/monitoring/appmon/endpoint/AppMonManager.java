package app.root.monitoring.appmon.endpoint;

import app.root.monitoring.appmon.group.GroupInfo;
import app.root.monitoring.appmon.group.GroupManager;
import app.root.monitoring.appmon.group.GroupManagerBuilder;
import app.root.monitoring.appmon.logtail.LogtailInfo;
import app.root.monitoring.appmon.logtail.LogtailManager;
import app.root.monitoring.appmon.logtail.LogtailManagerBuilder;
import app.root.monitoring.appmon.measurement.MeasurementInfo;
import app.root.monitoring.appmon.measurement.MeasurementManager;
import app.root.monitoring.appmon.measurement.MeasurementManagerBuilder;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContext;
import jakarta.websocket.Session;

import java.util.List;

/**
 * <p>Created: 4/3/24</p>
 */
public class AppMonManager {

    private final AppMonEndpoint endpoint;

    private final GroupManager groupManager;

    private final LogtailManager logtailManager;

    private final MeasurementManager measurementManager;

    public AppMonManager(AppMonEndpoint endpoint) throws Exception {
        this.endpoint = endpoint;
        this.groupManager = GroupManagerBuilder.build(this);
        this.logtailManager = LogtailManagerBuilder.build(this);
        this.measurementManager = MeasurementManagerBuilder.build(this);
    }

    public AppMonEndpoint getEndpoint() {
        return endpoint;
    }

    public ActivityContext getActivityContext() {
        return endpoint.getActivityContext();
    }

    public ApplicationAdapter getApplicationAdapter() {
        return getActivityContext().getApplicationAdapter();
    }

    public void broadcast(String message) {
        endpoint.broadcast(message);
    }

    public List<GroupInfo> getGroupInfoList(String[] joinGroups) {
        return groupManager.getGroupInfoList(joinGroups);
    }

    public List<LogtailInfo> getLogTailInfoList(String[] joinGroups) {
        return logtailManager.getLogTailInfoList(joinGroups);
    }

    public List<MeasurementInfo> getMeasurementInfoList(String[] joinGroups) {
        return measurementManager.getMeasurementInfoList(joinGroups);
    }

    public synchronized void join(Session session) {
        String[] joinGroups = groupManager.getJoinedGroups(session);
        logtailManager.join(joinGroups);
        measurementManager.join(joinGroups);
        if (joinGroups != null) {
            groupManager.saveJoinedGroups(session, joinGroups);
        } else {
            groupManager.removeJoinedGroups(session);
        }
    }

    public synchronized void release(Session session) {
        String[] unusedGroups = groupManager.getUnusedGroups(session);
        logtailManager.release(unusedGroups);
        measurementManager.release(unusedGroups);
        groupManager.removeJoinedGroups(session);
    }

}
