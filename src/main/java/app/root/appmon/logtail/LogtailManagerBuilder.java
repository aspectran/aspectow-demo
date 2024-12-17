package app.root.appmon.logtail;

import app.root.appmon.AppMonManager;
import app.root.appmon.config.LogtailInfo;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class LogtailManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LogtailManagerBuilder.class);

    @NonNull
    public static void build(@NonNull AppMonManager appMonManager,
                             @NonNull String groupName,
                             @NonNull List<LogtailInfo> logtailInfoList) throws IOException {
        LogtailManager logtailManager = new LogtailManager(appMonManager, groupName);
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
                LogtailService logtailService = new LogtailService(logtailManager, logTailInfo, logFile);
                logtailManager.addLogtailService(logTailInfo.getName(), logtailService);
            }
        }
        appMonManager.addLogtailManager(logtailManager);
    }

}
