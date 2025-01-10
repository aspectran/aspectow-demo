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
package com.aspectran.aspectow.appmon.exporter.state.session;

import com.aspectran.core.component.session.Session;
import com.aspectran.core.component.session.SessionListener;
import com.aspectran.utils.annotation.jsr305.NonNull;

import static com.aspectran.aspectow.appmon.exporter.state.session.SessionStateReader.USER_SESSION_KEY;

/**
 * <p>Created: 2024-12-13</p>
 */
public class SessionStateListener implements SessionListener {

    private final SessionStateReader statusReader;

    public SessionStateListener(SessionStateReader statusReader) {
        this.statusReader = statusReader;
    }

    @Override
    public void sessionCreated(@NonNull Session session) {
        String json = statusReader.readWithCreatedSession(session);
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void sessionDestroyed(@NonNull Session session) {
        String json = statusReader.readWithDestroyedSession(session.getId());
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void sessionEvicted(@NonNull Session session) {
        String json = statusReader.readWithEvictedSession(session.getId());
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void sessionResided(@NonNull Session session) {
        String json = statusReader.readWithResidedSession(session);
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
        if (USER_SESSION_KEY.equals(name)) {
            String json = statusReader.readWithCreatedSession(session);
            statusReader.getStateExporter().broadcast(json);
        }
    }

}
