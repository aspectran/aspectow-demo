package app.root.appmon.logtail;

import app.root.appmon.endpoint.AppMonManager;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class LogtailManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LogtailManagerBuilder.class);

    private static final String LOGTAIL_CONFIG_FILE = "app/root/appmon/logtail-config.apon";

    @NonNull
    public static LogtailManager build(@NonNull AppMonManager appMonManager) throws IOException {
        LogtailConfig logTailConfig = new LogtailConfig(ResourceUtils.getResourceAsReader(LOGTAIL_CONFIG_FILE));
        LogtailManager logtailManager = new LogtailManager(appMonManager);
        for (LogtailInfo logTailInfo : logTailConfig.getLogTailInfoList()) {
            File logFile = null;
            try {
                String file = appMonManager.getApplicationAdapter().toRealPath(logTailInfo.getFile());
                logFile = new File(file).getCanonicalFile();
            } catch (IOException e) {
                logger.error("Failed to resolve absolute path to log file " + logTailInfo.getFile(), e);
            }
            if (logFile != null) {
                LogTailer tailer = new LogTailer(logtailManager, logTailInfo, logFile);
                logtailManager.addLogTailer(logTailInfo.getName(), tailer);
            }
        }
        return logtailManager;
    }

}
