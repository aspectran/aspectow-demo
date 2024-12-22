package app.root.appmon.manager;

import app.root.appmon.config.AppMonConfig;
import app.root.appmon.config.AppMonConfigBuilder;
import com.aspectran.core.component.bean.ablility.FactoryBean;
import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Initialize;
import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.annotation.jsr305.NonNull;

/**
 * <p>Created: 2024-12-17</p>
 */
@Component
@Bean("appMonManager")
public class AppMonManagerFactoryBean implements ActivityContextAware, FactoryBean<AppMonManager> {

    private ActivityContext context;

    private AppMonManager appMonManager;

    @Override
    @AvoidAdvice
    public void setActivityContext(@NonNull ActivityContext context) {
        this.context = context;
    }

    @Initialize(profile = "!prod")
    public void createAppMonManager() throws Exception {
        AppMonConfig config = AppMonConfigBuilder.build(false);
        appMonManager = AppMonManagerBuilder.build(context, config);
    }

    @Initialize(profile = "prod")
    public void createAppMonManagerForProd() throws Exception {
        AppMonConfig config = AppMonConfigBuilder.build(true);
        appMonManager = AppMonManagerBuilder.build(context, config);
    }

    @Override
    public AppMonManager getObject() {
        return appMonManager;
    }

}
