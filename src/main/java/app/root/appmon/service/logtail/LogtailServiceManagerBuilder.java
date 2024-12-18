package app.root.appmon.service.logtail;

import app.root.appmon.config.LogtailInfo;
import app.root.appmon.manager.AppMonManager;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class LogtailServiceManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LogtailServiceManagerBuilder.class);

    @NonNull
    public static void build(@NonNull AppMonManager appMonManager,
                             @NonNull String groupName,
                             @NonNull List<LogtailInfo> logtailInfoList) throws IOException {
        LogtailServiceManager logtailServiceManager = new LogtailServiceManager(appMonManager, groupName);
        for (LogtailInfo logTailInfo : logtailInfoList) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create LogtailService", logTailInfo));
            }

            logTailInfo.validateRequiredParameters();

            File logFile = null;
            try {
                String file = appMonManager.getApplicationAdapter().toRealPath(logTailInfo.getFile());
                logFile = new File(file).getCanonicalFile();
            } catch (IOException e) {
                logger.error("Failed to resolve absolute path to log file " + logTailInfo.getFile(), e);
            }
            if (logFile != null) {
                LogtailService logtailService = new LogtailService(logtailServiceManager, logTailInfo, logFile);
                logtailServiceManager.addService(logtailService);
            }
        }
        appMonManager.addServiceManager(logtailServiceManager);
    }

}
