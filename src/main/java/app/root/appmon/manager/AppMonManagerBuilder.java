package app.root.appmon.manager;

import app.root.appmon.config.AppMonConfig;
import app.root.appmon.config.EndpointInfoHolder;
import app.root.appmon.config.EventInfo;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.GroupInfoHolder;
import app.root.appmon.config.LogInfo;
import app.root.appmon.config.StateInfo;
import app.root.appmon.exporter.event.EventExporterManager;
import app.root.appmon.exporter.event.EventExporterManagerBuilder;
import app.root.appmon.exporter.log.LogExporterManager;
import app.root.appmon.exporter.log.LogExporterManagerBuilder;
import app.root.appmon.exporter.state.StateExporterManager;
import app.root.appmon.exporter.state.StateExporterManagerBuilder;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.util.List;

/**
 * <p>Created: 2024-12-17</p>
 */
public abstract class AppMonManagerBuilder {

    @NonNull
    public static AppMonManager build(ActivityContext context, AppMonConfig config) throws Exception {
        Assert.notNull(context, "ActivityContext is not set");
        Assert.notNull(config, "AppMonConfig is not set");

        EndpointInfoHolder endpointInfoHolder = new EndpointInfoHolder(config.getEndpointInfoList());
        GroupInfoHolder groupInfoHolder = new GroupInfoHolder(config.getGroupInfoList());
        AppMonManager appMonManager = new AppMonManager(endpointInfoHolder, groupInfoHolder);
        appMonManager.setActivityContext(context);
        for (GroupInfo groupInfo : config.getGroupInfoList()) {
            List<EventInfo> eventInfoList = config.getEventInfoList(groupInfo.getName());
            if (eventInfoList != null && !eventInfoList.isEmpty()) {
                EventExporterManager eventExporterManager = appMonManager.newEventExporterManager(groupInfo.getName());
                EventExporterManagerBuilder.build(eventExporterManager, eventInfoList);
            }
            List<StateInfo> stateInfoList = config.getStateInfoList(groupInfo.getName());
            if (stateInfoList != null && !stateInfoList.isEmpty()) {
                StateExporterManager stateExporterManager = appMonManager.newStateExporterManager(groupInfo.getName());
                StateExporterManagerBuilder.build(stateExporterManager, stateInfoList);
            }
            List<LogInfo> logInfoList = config.getLogtailInfoList(groupInfo.getName());
            if (logInfoList != null && !logInfoList.isEmpty()) {
                LogExporterManager logExporterManager = appMonManager.newLogExporterManager(groupInfo.getName());
                LogExporterManagerBuilder.build(logExporterManager, logInfoList, context.getApplicationAdapter());
            }
        }
        return appMonManager;
    }

}
