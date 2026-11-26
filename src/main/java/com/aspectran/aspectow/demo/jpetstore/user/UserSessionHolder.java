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
package com.aspectran.aspectow.demo.jpetstore.user;

import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import org.jspecify.annotations.NonNull;

@Component
@Bean("userSessionHolder")
public class UserSessionHolder extends InstantActivitySupport {

    public static final String USER_SESSION_KEY = "user";

    public UserSession get() {
        return get(true);
    }

    public UserSession get(boolean create) {
        try {
            UserSession userSession = getSessionAdapter().getAttribute(USER_SESSION_KEY);
            if (userSession == null && create) {
                synchronized (this) {
                    userSession = getSessionAdapter().getAttribute(USER_SESSION_KEY);
                    if (userSession == null) {
                        userSession = new UserSession();
                        save(userSession);
                    }
                }
            }
            return userSession;
        } catch (ClassCastException e) {
            // Exception that can occur if the UserSession class changes during development.
            if (create) {
                UserSession userSession = new UserSession();
                save(userSession);
                return userSession;
            } else {
                expire();
                return null;
            }
        }
    }

    public void save(UserSession userSession) {
        getSessionAdapter().setAttribute(USER_SESSION_KEY, userSession);
    }

    public void expire() {
        getSessionAdapter().invalidate();
    }

    public boolean isAuthenticated() {
        UserSession userSession = get(false);
        return (userSession != null && userSession.isAuthenticated());
    }

    @NonNull
    private SessionAdapter getSessionAdapter() {
        return getActivityContext().getCurrentActivity().getSessionAdapter();
    }

}
