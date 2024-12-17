package app.root.appmon.status;

import app.root.appmon.AppMonManager;
import app.root.appmon.config.StatusInfo;
import com.aspectran.utils.ClassUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.List;

public abstract class StatusManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StatusManagerBuilder.class);

    @NonNull
    public static StatusManager build(@NonNull AppMonManager appMonManager,
                                      @NonNull String groupName,
                                      @NonNull List<StatusInfo> statusInfoList) throws Exception {
        StatusManager statusManager = new StatusManager(appMonManager, groupName);
        for (StatusInfo statusInfo : statusInfoList) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create StatusService", statusInfo));
            }
            statusInfo.validateRequiredParameters();

            StatusReader statusReader = createStatusReader(statusManager, statusInfo);
            StatusService statusService = new StatusService(statusManager, statusInfo, statusReader);
            statusManager.addStatusService(statusInfo.getName(), statusService);
            appMonManager.addStatusManager(statusManager);
        }
        return statusManager;
    }

    @NonNull
    private static StatusReader createStatusReader(
            @NonNull StatusManager statusManager, @NonNull StatusInfo statusInfo) throws Exception {
        try {
            Class<StatusReader> collectorType = ClassUtils.classForName(statusInfo.getCollector());
            Object[] args = { statusManager, statusInfo };
            Class<?>[] argTypes = { StatusManager.class, StatusInfo.class };
            return ClassUtils.createInstance(collectorType, args, argTypes);
        } catch (Exception e) {
            throw new Exception("Failed to create status reader: " + statusInfo, e);
        }
    }

}