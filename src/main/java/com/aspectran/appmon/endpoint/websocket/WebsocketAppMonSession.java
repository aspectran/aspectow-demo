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
package com.aspectran.appmon.endpoint.websocket;

import com.aspectran.appmon.endpoint.AppMonSession;
import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.Nullable;
import jakarta.websocket.Session;

public class WebsocketAppMonSession implements AppMonSession {

    private static final String JOINED_GROUPS_PROPERTY = "appmon/JoinedGroups";

    private final Session session;

    public WebsocketAppMonSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public String[] getJoinedGroups() {
        return (String[])session.getUserProperties().get(JOINED_GROUPS_PROPERTY);
    }

    @Override
    public void saveJoinedGroups(String[] joinGroups) {
        Assert.notEmpty(joinGroups, "joinGroups must not be null or empty");
        session.getUserProperties().put(JOINED_GROUPS_PROPERTY, joinGroups);
    }

    @Override
    public void removeJoinedGroups() {
        session.getUserProperties().remove(JOINED_GROUPS_PROPERTY);
    }

    @Override
    public boolean isTwoWay() {
        return true;
    }

    @Override
    public boolean isValid() {
        return session.isOpen();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other || session == other) {
            return true;
        }
        if (other instanceof WebsocketAppMonSession that) {
            return session.equals(that.getSession());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

}
