/*
 * Copyright (c) 2008-2024 The Aspectran Project
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
package app.root.appmon.service.status.session;

import com.aspectran.utils.json.JsonBuilder;
import com.aspectran.utils.json.JsonString;

/**
 * <p>Created: 2020/01/11</p>
 */
public class SessionStatusPayload {

    private long createdSessionCount;

    private long expiredSessionCount;

    private long activeSessionCount;

    private long highestActiveSessionCount;

    private long evictedSessionCount;

    private long rejectedSessionCount;

    private String elapsedTime;

    private JsonString[] createdSessions;

    private String[] destroyedSessions;

    public long getCreatedSessionCount() {
        return createdSessionCount;
    }

    public void setCreatedSessionCount(long createdSessionCount) {
        this.createdSessionCount = createdSessionCount;
    }

    public long getExpiredSessionCount() {
        return expiredSessionCount;
    }

    public void setExpiredSessionCount(long expiredSessionCount) {
        this.expiredSessionCount = expiredSessionCount;
    }

    public long getEvictedSessionCount() {
        return evictedSessionCount;
    }

    public long getActiveSessionCount() {
        return activeSessionCount;
    }

    public void setActiveSessionCount(long activeSessionCount) {
        this.activeSessionCount = activeSessionCount;
    }

    public void setEvictedSessionCount(long evictedSessionCount) {
        this.evictedSessionCount = evictedSessionCount;
    }

    public long getHighestActiveSessionCount() {
        return highestActiveSessionCount;
    }

    public void setHighestActiveSessionCount(long highestActiveSessionCount) {
        this.highestActiveSessionCount = highestActiveSessionCount;
    }

    public long getRejectedSessionCount() {
        return rejectedSessionCount;
    }

    public void setRejectedSessionCount(long rejectedSessionCount) {
        this.rejectedSessionCount = rejectedSessionCount;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public JsonString[] getCreatedSessions() {
        return createdSessions;
    }

    public void setCreatedSessions(JsonString[] createdSessions) {
        this.createdSessions = createdSessions;
    }

    public String[] getDestroyedSessions() {
        return destroyedSessions;
    }

    public void setDestroyedSessions(String[] destroyedSessions) {
        this.destroyedSessions = destroyedSessions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SessionStatusPayload that)) {
            return false;
        }
        return that.createdSessionCount == createdSessionCount &&
                that.expiredSessionCount == expiredSessionCount &&
                that.evictedSessionCount == evictedSessionCount &&
                that.activeSessionCount == activeSessionCount &&
                that.highestActiveSessionCount == highestActiveSessionCount &&
                that.rejectedSessionCount == rejectedSessionCount;
    }

    public String toJson() {
        return new JsonBuilder()
                .prettyPrint(false)
                .put(this)
                .toString();
    }

}
