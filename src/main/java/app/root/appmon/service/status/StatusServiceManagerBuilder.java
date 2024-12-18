package app.root.appmon.service.status;

import app.root.appmon.config.StatusInfo;
import app.root.appmon.manager.AppMonManager;
import com.aspectran.utils.ClassUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.List;

public abstract class StatusServiceManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StatusServiceManagerBuilder.class);

    @NonNull
    public static StatusServiceManager build(@NonNull AppMonManager appMonManager,
                                             @NonNull String groupName,
                                             @NonNull List<StatusInfo> statusInfoList) throws Exception {
        StatusServiceManager statusServiceManager = new StatusServiceManager(appMonManager, groupName);
        for (StatusInfo statusInfo : statusInfoList) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create StatusService", statusInfo));
            }
            statusInfo.validateRequiredParameters();

            StatusReader statusReader = createStatusReader(statusServiceManager, statusInfo);
            StatusService statusService = new StatusService(statusServiceManager, statusInfo, statusReader);
            statusServiceManager.addService(statusService);
            appMonManager.addServiceManager(statusServiceManager);
        }
        return statusServiceManager;
    }

    @NonNull
    private static StatusReader createStatusReader(
            @NonNull StatusServiceManager statusServiceManager, @NonNull StatusInfo statusInfo) throws Exception {
        try {
            Class<StatusReader> collectorType = ClassUtils.classForName(statusInfo.getReader());
            Object[] args = { statusServiceManager, statusInfo };
            Class<?>[] argTypes = { StatusServiceManager.class, StatusInfo.class };
            return ClassUtils.createInstance(collectorType, args, argTypes);
        } catch (Exception e) {
            throw new Exception("Failed to create status reader: " + statusInfo, e);
        }
    }

}