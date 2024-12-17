/*
 * Copyright (c) ${project.inceptionYear}-2024 The Aspectran Project
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
package app.root.appmon.logtail;

import app.root.appmon.AppMonManager;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogtailManager {

    private static final Logger logger = LoggerFactory.getLogger(LogtailManager.class);

    private final Map<String, LogtailService> logtailServices = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    private final String groupName;

    public LogtailManager(AppMonManager appMonManager, String groupName) {
        this.appMonManager = appMonManager;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public Collection<LogtailService> getLogtailServices() {
        return logtailServices.values();
    }

    public void addLogtailService(String name, LogtailService service) {
        logtailServices.put(name, service);
    }

    public void start() {
        for (LogtailService service : logtailServices.values()) {
            try {
                service.start();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void stop() {
        for (LogtailService service : logtailServices.values()) {
            try {
                service.stop();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void collectLastLogs(List<String> messages) {
        for (LogtailService service : logtailServices.values()) {
            service.readLastLines(messages);
        }
    }

    void broadcast(String message) {
        appMonManager.broadcast(message);
    }

}
