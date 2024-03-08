package app.root.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import ch.qos.logback.core.util.OptionHelper;
import com.aspectran.web.service.WebService;
import com.aspectran.web.service.WebServiceHolder;

public class WebServiceBasedDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

    private String key;

    private String defaultValue;

    /**
     * Return the value associated with an MDC entry designated by the Key property.
     * If that value is null, then return the value assigned to the DefaultValue
     * property.
     */
    @Override
    public String getDiscriminatingValue(ILoggingEvent event) {
        WebService webService = WebServiceHolder.acquire();
        if (webService == null) {
            return defaultValue;
        }
        String value = webService.getServletContext().getInitParameter(key);
        return (value != null ? value : defaultValue);
    }

    @Override
    public void start() {
        int errors = 0;
        if (OptionHelper.isNullOrEmptyOrAllSpaces(key)) {
            errors++;
            addError("The \"Key\" property must be set");
        }
        if (OptionHelper.isNullOrEmptyOrAllSpaces(defaultValue)) {
            errors++;
            addError("The \"DefaultValue\" property must be set");
        }
        if (errors == 0) {
            started = true;
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
