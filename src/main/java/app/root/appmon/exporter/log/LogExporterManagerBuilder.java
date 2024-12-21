package app.root.appmon.exporter.log;

import app.root.appmon.config.LogInfo;
import app.root.appmon.manager.AppMonManager;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class LogExporterManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LogExporterManagerBuilder.class);

    @NonNull
    public static void build(@NonNull AppMonManager appMonManager,
                             @NonNull String groupName,
                             @NonNull List<LogInfo> logInfoList) throws IOException {
        LogExporterManager logExporterManager = new LogExporterManager(appMonManager, groupName);
        for (LogInfo logInfo : logInfoList) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create LogExporter", logInfo));
            }

            logInfo.validateRequiredParameters();

            File logFile = null;
            try {
                String file = appMonManager.getApplicationAdapter().toRealPath(logInfo.getFile());
                logFile = new File(file).getCanonicalFile();
            } catch (IOException e) {
                logger.error("Failed to resolve absolute path to log file " + logInfo.getFile(), e);
            }
            if (logFile != null) {
                LogExporter logExporter = new LogExporter(logExporterManager, logInfo, logFile);
                logExporterManager.addExporter(logExporter);
            }
        }
        appMonManager.addExporterManager(logExporterManager);
    }

}
