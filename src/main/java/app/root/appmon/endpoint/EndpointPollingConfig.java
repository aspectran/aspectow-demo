package app.root.appmon.endpoint;

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

public class EndpointPollingConfig extends AbstractParameters {

    private static final ParameterKey pollingInterval;
    private static final ParameterKey sessionTimeout;

    private static final ParameterKey[] parameterKeys;

    static {
        pollingInterval = new ParameterKey("pollingInterval", ValueType.INT);
        sessionTimeout = new ParameterKey("sessionTimeout", ValueType.INT);

        parameterKeys = new ParameterKey[] {
                pollingInterval,
                sessionTimeout
        };
    }

    public EndpointPollingConfig() {
        super(parameterKeys);
    }

    public int getPollingInterval() {
        return getInt(pollingInterval, 0);
    }

    public void setPollingInterval(int pollingInterval) {
        putValue(EndpointPollingConfig.pollingInterval, pollingInterval);
    }

    public int getSessionTimeout() {
        return getInt(pollingInterval, 0);
    }

    public void setSessionTimeout(int sessionTimeout) {
        putValue(EndpointPollingConfig.sessionTimeout, sessionTimeout);
    }

}
