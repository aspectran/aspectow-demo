package app.root.appmon.measurement;

import app.root.appmon.endpoint.AppMonManager;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.IOException;

public class MeasurementManagerBuilder {

    private static final String MEASUREMENT_CONFIG_FILE = "app/root/appmon/measurement-config.apon";

    private static final Logger logger = LoggerFactory.getLogger(MeasurementManagerBuilder.class);

    public static MeasurementManager build(AppMonManager appMonManager) throws IOException {
        MeasurementConfig logTailConfig = new MeasurementConfig(ResourceUtils.getResourceAsReader(MEASUREMENT_CONFIG_FILE));
        MeasurementManager measurementManager = new MeasurementManager(appMonManager);
        for (MeasurementInfo measurementInfo : logTailConfig.getMeasurementInfoList()) {
            Measuring measuring = new Measuring(measurementManager, measurementInfo);
            measurementManager.addMeasuring(measurementInfo.getName(), measuring);
        }
        return measurementManager;
    }

}
