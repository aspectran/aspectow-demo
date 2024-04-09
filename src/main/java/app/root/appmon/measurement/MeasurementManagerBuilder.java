package app.root.appmon.measurement;

import app.root.appmon.endpoint.AppMonManager;
import com.aspectran.utils.Assert;
import com.aspectran.utils.ClassUtils;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

public abstract class MeasurementManagerBuilder {

    private static final String MEASUREMENT_CONFIG_FILE = "app/root/appmon/measurement-config.apon";

    private static final Logger logger = LoggerFactory.getLogger(MeasurementManagerBuilder.class);

    @NonNull
    public static MeasurementManager build(@NonNull AppMonManager appMonManager) throws Exception {
        MeasurementConfig logTailConfig = new MeasurementConfig(ResourceUtils.getResourceAsReader(MEASUREMENT_CONFIG_FILE));
        MeasurementManager measurementManager = new MeasurementManager(appMonManager);
        for (MeasurementInfo measurementInfo : logTailConfig.getMeasurementInfoList()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating Measuring " + ToStringBuilder.toString(measurementInfo));
            }
            validateRequiredParameter(measurementInfo, MeasurementInfo.group);
            validateRequiredParameter(measurementInfo, MeasurementInfo.name);
            validateRequiredParameter(measurementInfo, MeasurementInfo.source);
            validateRequiredParameter(measurementInfo, MeasurementInfo.collector);

            MeasureCollector dataCollector = createMeasureDataCollector(measurementManager, measurementInfo);
            Measuring measuring = new Measuring(measurementManager, measurementInfo, dataCollector);
            measurementManager.addMeasuring(measurementInfo.getName(), measuring);
        }
        return measurementManager;
    }

    private static void validateRequiredParameter(@NonNull Parameters parameters, ParameterKey key) {
        Assert.hasLength(parameters.getString(key),
                "Missing value of required parameter: " + parameters.getQualifiedName(key));
    }

    @NonNull
    private static MeasureCollector createMeasureDataCollector(@NonNull MeasurementManager measurementManager,
                                                               @NonNull MeasurementInfo measurementInfo)
            throws Exception {
        try {
            Class<MeasureCollector> collectorType = ClassUtils.classForName(measurementInfo.getCollector());
            Object[] args = {measurementManager, measurementInfo};
            Class<?>[] argTypes = {MeasurementManager.class, MeasurementInfo.class};
            return ClassUtils.createInstance(collectorType, args, argTypes);
        } catch (Exception e) {
            throw new Exception("Failed to create measure data collector: " + measurementInfo.getCollector(), e);
        }
    }

}
