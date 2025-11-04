/*
 * Copyright (c) 2018-present The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.root.common.listener;

import app.root.common.IPToCountryLookup;
import app.root.util.TransletUtils;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.session.Session;
import com.aspectran.core.component.session.SessionListener;
import com.aspectran.core.component.session.SessionListenerRegistration;
import com.aspectran.undertow.server.TowServer;
import com.aspectran.undertow.support.SessionListenerRegistrationBean;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.util.Locale;

import static com.aspectran.appmon.engine.exporter.event.session.SessionEventReader.USER_COUNTRY_CODE;
import static com.aspectran.appmon.engine.exporter.event.session.SessionEventReader.USER_IP_ADDRESS;

/**
 * <p>Created: 2024-12-13</p>
 */
@Component
public class UserTrackingListener extends InstantActivitySupport implements SessionListener, InitializableBean {

    private final IPToCountryLookup ipToCountryLookup;

    @Autowired
    public UserTrackingListener(IPToCountryLookup ipToCountryLookup) {
        this.ipToCountryLookup = ipToCountryLookup;
    }

    @Override
    public void sessionCreated(@NonNull Session session) {
        Activity activity = getCurrentActivity();
        String ipAddress = TransletUtils.getRemoteAddr(activity.getTranslet());
        if (!StringUtils.isEmpty(ipAddress)) {
            session.setAttribute(USER_IP_ADDRESS, ipAddress);
            Locale locale = activity.getTranslet().getRequestAdapter().getLocale();
            String countryCode = ipToCountryLookup.getCountryCode(ipAddress, locale);
            if (StringUtils.hasLength(countryCode)) {
                session.setAttribute(USER_COUNTRY_CODE, countryCode);
            }
        }
    }

    @Override
    public void initialize() throws Exception {
        SessionListenerRegistration sessionListenerRegistration;
        if (getBeanRegistry().containsBean(SessionListenerRegistration.class)) {
            sessionListenerRegistration = getBeanRegistry().getBean(SessionListenerRegistration.class);
        } else {
            if (getBeanRegistry().containsBean(TowServer.class)) {
                sessionListenerRegistration = new SessionListenerRegistrationBean();
            } else {
                throw new IllegalStateException("Bean for SessionListenerRegistration must be defined");
            }
        }
        sessionListenerRegistration.register(this, getActivityContext().getName());
    }

}
