package app.appmon.group;

import app.appmon.endpoint.AppMonManager;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.io.IOException;

public abstract class GroupManagerBuilder {

    private static final String MEASUREMENT_CONFIG_FILE = "app/appmon/group-config.apon";

    @NonNull
    public static GroupManager build(@NonNull AppMonManager appMonManager) throws IOException {
        GroupConfig groupConfig = new GroupConfig(ResourceUtils.getResourceAsReader(MEASUREMENT_CONFIG_FILE));
        return new GroupManager(appMonManager, groupConfig.getGroupInfoList());
    }

}
