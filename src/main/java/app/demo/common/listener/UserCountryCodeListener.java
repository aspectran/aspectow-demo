package app.demo.common.listener;

import app.root.util.CountryCodeLookup;
import app.root.util.TransletUtils;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.session.Session;
import com.aspectran.core.component.session.SessionListener;
import com.aspectran.core.component.session.SessionListenerRegistration;
import com.aspectran.utils.StringUtils;

/**
 * <p>Created: 2024-12-13</p>
 */
@Component
@AvoidAdvice
public class UserCountryCodeListener extends InstantActivitySupport implements SessionListener, InitializableBean {

    @Override
    public void sessionCreated(Session session) {
        Activity activity = getCurrentActivity();
        SessionAdapter sessionAdapter = activity.getSessionAdapter();
        if (StringUtils.isEmpty(sessionAdapter.getAttribute("countryCode"))) {
            String remoteAddr = TransletUtils.getRemoteAddr(activity.getTranslet());
            String countryCode = CountryCodeLookup.getInstance().getCountryCodeByIP(remoteAddr);
            if (StringUtils.hasLength(countryCode)) {
                activity.getSessionAdapter().setAttribute("countryCode", countryCode);
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
