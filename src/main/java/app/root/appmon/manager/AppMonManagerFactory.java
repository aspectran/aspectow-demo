package app.root.appmon.manager;

import app.root.appmon.config.AppMonConfig;
import app.root.appmon.config.EndpointInfoHolder;
import app.root.appmon.config.EventInfo;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.GroupInfoHolder;
import app.root.appmon.config.LogtailInfo;
import app.root.appmon.config.StatusInfo;
import app.root.appmon.service.event.EventServiceManagerBuilder;
import app.root.appmon.service.logtail.LogtailServiceManagerBuilder;
import app.root.appmon.service.status.StatusServiceManagerBuilder;
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
                EventServiceManagerBuilder.build(appMonManager, groupInfo.getName(), eventInfoList);
            }
            List<StatusInfo> statusInfoList = config.getStatusInfoList(groupInfo.getName());
            if (statusInfoList != null) {
                StatusServiceManagerBuilder.build(appMonManager, groupInfo.getName(), statusInfoList);
            }
            List<LogtailInfo> logtailInfoList = config.getLogtailInfoList(groupInfo.getName());
            if (logtailInfoList != null) {
                LogtailServiceManagerBuilder.build(appMonManager, groupInfo.getName(), logtailInfoList);
            }
        }
        return appMonManager;
    }

}
