package app.root.appmon;

import app.root.appmon.config.AppMonConfig;
import app.root.appmon.config.AppMonConfigBuilder;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.GroupManager;
import app.root.appmon.config.LogtailInfo;
import app.root.appmon.config.StatusInfo;
import app.root.appmon.endpoint.EndpointManager;
import app.root.appmon.logtail.LogtailManagerBuilder;
import app.root.appmon.status.StatusManagerBuilder;
import com.aspectran.core.component.bean.ablility.FactoryBean;
import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Initialize;
import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.util.List;

/**
 * <p>Created: 2024-12-17</p>
 */
@Component
@Bean("appMonManager")
public class AppMonManagerFactoryBean implements ActivityContextAware, FactoryBean<AppMonManager> {

    private ActivityContext context;

    private AppMonConfig config;

    @NonNull
    protected ActivityContext getActivityContext() {
        Assert.state(context != null, "No ActivityContext injected");
        return context;
    }

    @Override
    @AvoidAdvice
    public void setActivityContext(@NonNull ActivityContext context) {
        Assert.state(this.context == null, "ActivityContext already injected");
        this.context = context;
    }

    @Initialize(profile = "!prod")
    public void createAppMonConfig() throws Exception {
        Assert.state(config == null, "AppMonConfig is already created");
        config = AppMonConfigBuilder.build(false);
    }

    @Initialize(profile = "prod")
    public void createAppMonConfigForProd() throws Exception {
        Assert.state(config == null, "AppMonConfig is already created");
        config = AppMonConfigBuilder.build(true);
    }

    @Override
    public AppMonManager getObject() throws Exception {
        Assert.state(config != null, "AppMonConfig is not created");

        EndpointManager endpointManager = new EndpointManager(config.getEndpointInfoList());
        GroupManager groupManager = new GroupManager(config.getGroupInfoList());
        AppMonManager appMonManager = new AppMonManager(endpointManager, groupManager);
        appMonManager.setActivityContext(getActivityContext());

        for (GroupInfo groupInfo : config.getGroupInfoList()) {
            List<StatusInfo> statusInfoList = config.getStatusInfoList(groupInfo.getName());
            if (statusInfoList != null) {
                StatusManagerBuilder.build(appMonManager, groupInfo.getName(), statusInfoList);
            }
            List<LogtailInfo> logtailInfoList = config.getLogtailInfoList(groupInfo.getName());
            if (logtailInfoList != null) {
                LogtailManagerBuilder.build(appMonManager, groupInfo.getName(), logtailInfoList);
            }
        }

        return appMonManager;
    }


}
