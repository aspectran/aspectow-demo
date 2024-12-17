package app.root.appmon.config;

import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.io.IOException;
import java.io.Reader;

public abstract class AppMonConfigBuilder {

    private static final String APPMON_CONFIG_FILE = "app/root/appmon/appmon-config.apon";

    private static final String APPMON_CONFIG_FILE_PROD = "app/root/appmon/appmon-config-prod.apon";

    @NonNull
    public static AppMonConfig build(boolean forProd) throws IOException {
        Reader reader;
        if (forProd) {
            reader = ResourceUtils.getResourceAsReader(APPMON_CONFIG_FILE_PROD);
        } else {
            reader = ResourceUtils.getResourceAsReader(APPMON_CONFIG_FILE);
        }
        return new AppMonConfig(reader);
    }

}
