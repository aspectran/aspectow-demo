package app.root.appmon.manager;

import app.root.appmon.config.AppMonConfig;
import app.root.appmon.config.EndpointInfoHolder;
import app.root.appmon.config.EventInfo;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.GroupInfoHolder;
import app.root.appmon.config.LogInfo;
import app.root.appmon.config.StateInfo;
import app.root.appmon.exporter.event.EventExporterManagerBuilder;
import app.root.appmon.exporter.log.LogExporterManagerBuilder;
import app.root.appmon.exporter.state.StateExporterManagerBuilder;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.Assert;

import java.util.List;

/**
 * <p>Created: 2024-12-17</p>
 */
public class AppMonManagerFactory {

    private ActivityContext context;

    private AppMonConfig config;

    public AppMonManagerFactory() {
    }

    public void setActivityContext(ActivityContext context) {
        this.context = context;
    }

    public void setAppMonConfig(AppMonConfig config) {
        this.config = config;
    }

    public AppMonManager createAppMonManager() throws Exception {
        Assert.state(context != null, "ActivityContext is not set");
        Assert.state(config != null, "AppMonConfig is not set");

        EndpointInfoHolder endpointInfoHolder = new EndpointInfoHolder(config.getEndpointInfoList());
        GroupInfoHolder groupInfoHolder = new GroupInfoHolder(config.getGroupInfoList());
        AppMonManager appMonManager = new AppMonManager(endpointInfoHolder, groupInfoHolder);
        appMonManager.setActivityContext(context);
        for (GroupInfo groupInfo : config.getGroupInfoList()) {
            List<EventInfo> eventInfoList = config.getEventInfoList(groupInfo.getName());
            if (eventInfoList != null) {
                EventExporterManagerBuilder.build(appMonManager, groupInfo.getName(), eventInfoList);
            }
            List<StateInfo> stateInfoList = config.getStateInfoList(groupInfo.getName());
            if (stateInfoList != null) {
                StateExporterManagerBuilder.build(appMonManager, groupInfo.getName(), stateInfoList);
            }
            List<LogInfo> logInfoList = config.getLogtailInfoList(groupInfo.getName());
            if (logInfoList != null) {
                LogExporterManagerBuilder.build(appMonManager, groupInfo.getName(), logInfoList);
            }
        }
        return appMonManager;
    }

}
