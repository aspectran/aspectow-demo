package app.root.appmon.exporter.state;

import app.root.appmon.config.StateInfo;
import app.root.appmon.manager.AppMonManager;
import com.aspectran.utils.ClassUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.List;

public abstract class StateExporterManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StateExporterManagerBuilder.class);

    @NonNull
    public static StateExporterManager build(@NonNull AppMonManager appMonManager,
                                             @NonNull String groupName,
                                             @NonNull List<StateInfo> stateInfoList) throws Exception {
        StateExporterManager stateExporterManager = new StateExporterManager(appMonManager, groupName);
        for (StateInfo stateInfo : stateInfoList) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create StateExporter", stateInfo));
            }
            stateInfo.validateRequiredParameters();

            StateReader stateReader = createStateReader(stateExporterManager, stateInfo);
            StateExporter stateExporter = new StateExporter(stateExporterManager, stateInfo, stateReader);
            stateExporterManager.addExporter(stateExporter);
            appMonManager.addExporterManager(stateExporterManager);
        }
        return stateExporterManager;
    }

    @NonNull
    private static StateReader createStateReader(
            @NonNull StateExporterManager stateExporterManager, @NonNull StateInfo stateInfo) throws Exception {
        try {
            Class<StateReader> readerType = ClassUtils.classForName(stateInfo.getReader());
            Object[] args = { stateExporterManager, stateInfo };
            Class<?>[] argTypes = { StateExporterManager.class, StateInfo.class };
            return ClassUtils.createInstance(readerType, args, argTypes);
        } catch (Exception e) {
            throw new Exception("Failed to create state reader: " + stateInfo, e);
        }
    }

}