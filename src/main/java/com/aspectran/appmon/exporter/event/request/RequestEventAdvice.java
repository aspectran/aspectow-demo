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
package com.aspectran.appmon.exporter.event.request;

import com.aspectran.core.activity.Activity;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.json.JsonBuilder;

/**
 * <p>Created: 2024-12-19</p>
 */
public class RequestEventAdvice {

    private final long number;

    private long startTime;

    private String sessionId;

    public RequestEventAdvice(long number) {
        this.number = number;
    }

    public void request(@NonNull Activity activity) {
        startTime = System.currentTimeMillis();

        // Since the servlet container does not allow session creation after
        // the response is committed, the session ID must be secured in advance.
        if (activity.hasSessionAdapter()) {
            sessionId = activity.getSessionAdapter().getId();
        }
    }

    public String complete(@NonNull Activity activity) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        Throwable error = activity.getRootCauseOfRaisedException();

        return new JsonBuilder()
                .prettyPrint(false)
                .nullWritable(false)
                .object()
                    .put("number", number)
                    .put("startTime", startTime)
                    .put("elapsedTime", elapsedTime)
                    .put("thread", Thread.currentThread().getName())
                    .put("sessionId", sessionId)
                    .put("error", error)
                .endObject()
                .toString();
    }

}
