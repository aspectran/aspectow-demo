package app.jpetstore.common.listener;

import app.root.util.IPToCountryLookup;
import app.root.util.TransletUtils;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.session.Session;
import com.aspectran.core.component.session.SessionListener;
import com.aspectran.core.component.session.SessionListenerRegistration;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.Locale;

/**
 * <p>Created: 2024-12-13</p>
 */
@Component
@AvoidAdvice
public class UserCountryLookupListener extends InstantActivitySupport implements SessionListener, InitializableBean {

    private static final Logger logger = LoggerFactory.getLogger(app.root.common.listener.UserCountryLookupListener.class);

    @Override
    public void sessionCreated(@NonNull Session session) {
        Activity activity = getCurrentActivity();
        String ipAddress = TransletUtils.getRemoteAddr(activity.getTranslet());
        if (!StringUtils.isEmpty(ipAddress)) {
            session.setAttribute("user.ipAddress", ipAddress);
            Locale locale = activity.getTranslet().getRequestAdapter().getLocale();
            String countryCode = IPToCountryLookup.getInstance().getCountryCode(ipAddress, locale);
            if (StringUtils.hasLength(countryCode)) {
                session.setAttribute("user.countryCode", countryCode);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Country code of IP address " + ipAddress + ": " +
                        (countryCode == null ? "None" : countryCode));
            }
        }
    }

    @Override
    public void initialize() throws Exception {
        SessionListenerRegistration sessionListenerRegistration =
                getBeanRegistry().getBean(SessionListenerRegistration.class);
        if (sessionListenerRegistration == null) {
            throw new IllegalStateException("Bean for SessionListenerRegistration must be defined");
        }
        sessionListenerRegistration.register(this, getActivityContext().getName());
    }

}
