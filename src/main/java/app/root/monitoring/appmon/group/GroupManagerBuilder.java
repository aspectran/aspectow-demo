package app.root.monitoring.appmon.group;

import app.root.monitoring.appmon.endpoint.AppMonManager;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.IOException;

public class GroupManagerBuilder {

    private static final String MEASUREMENT_CONFIG_FILE = "app/root/appmon-group-config.apon";

    private static final Logger logger = LoggerFactory.getLogger(GroupManagerBuilder.class);

    public static GroupManager build(AppMonManager appMonManager) throws IOException {
        GroupConfig groupConfig = new GroupConfig(ResourceUtils.getResourceAsReader(MEASUREMENT_CONFIG_FILE));
        return new GroupManager(appMonManager, groupConfig.getGroupInfoList());
    }

}
