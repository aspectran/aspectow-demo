package app.root.appmon.manager;

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
public class AppMonManagerFactoryBean extends AppMonManagerFactory
        implements ActivityContextAware, FactoryBean<AppMonManager> {

    @Override
    @AvoidAdvice
    public void setActivityContext(@NonNull ActivityContext context) {
        super.setActivityContext(context);
    }

    @Initialize(profile = "!prod")
    public void createAppMonConfig() throws Exception {
        setAppMonConfig(AppMonConfigBuilder.build(false));
    }

    @Initialize(profile = "prod")
    public void createAppMonConfigForProd() throws Exception {
        setAppMonConfig(AppMonConfigBuilder.build(true));
    }

    @Override
    public AppMonManager getObject() throws Exception {
        return createAppMonManager();
    }

}
