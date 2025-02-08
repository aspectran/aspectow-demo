/*
 * Copyright (c) 2018-2025 The Aspectran Project
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

import java.util.Locale;

/**
 * <p>Created: 2024-12-13</p>
 */
@Component
@AvoidAdvice
public class UserTrackingListener extends InstantActivitySupport implements SessionListener, InitializableBean {

    private static final String USER_IP_ADDRESS = "user.ipAddress";

    private static final String USER_COUNTRY_CODE = "user.countryCode";

    @Override
    public void sessionCreated(@NonNull Session session) {
        Activity activity = getCurrentActivity();
        String ipAddress = TransletUtils.getRemoteAddr(activity.getTranslet());
        if (!StringUtils.isEmpty(ipAddress)) {
            session.setAttribute(USER_IP_ADDRESS, ipAddress);
            Locale locale = activity.getTranslet().getRequestAdapter().getLocale();
            String countryCode = IPToCountryLookup.getInstance().getCountryCode(ipAddress, locale);
            if (StringUtils.hasLength(countryCode)) {
                session.setAttribute(USER_COUNTRY_CODE, countryCode);
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
