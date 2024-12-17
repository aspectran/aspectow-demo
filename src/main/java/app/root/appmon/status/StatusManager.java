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
package app.root.appmon.status;

import app.root.appmon.AppMonManager;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatusManager {

    private static final Logger logger = LoggerFactory.getLogger(StatusManager.class);

    private final Map<String, StatusService> statusServices = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    private final String groupName;

    public StatusManager(AppMonManager appMonManager, String groupName) {
        this.appMonManager = appMonManager;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public Collection<StatusService> getStatusServices() {
        return statusServices.values();
    }

    void addStatusService(String name, StatusService statusService) {
        statusServices.put(name, statusService);
    }

    public StatusService getStatusService(String name) {
        StatusService statusService = statusServices.get(name);
        if (statusService == null) {
            throw new IllegalArgumentException("No status service found for name " + name);
        }
        return statusService;
    }

    public void start() {
        for (StatusService service : statusServices.values()) {
            try {
                service.start();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void stop() {
        for (StatusService service : statusServices.values()) {
            try {
                service.stop();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void collectStatuses(List<String> messages) {
        for (StatusService service : statusServices.values()) {
            service.readStatus(messages);
        }
    }

    public <V> V getBean(@NonNull String id) {
        return appMonManager.getActivityContext().getBeanRegistry().getBean(id);
    }

    public <V> V getBean(Class<V> type) {
        return appMonManager.getActivityContext().getBeanRegistry().getBean(type);
    }

    public void broadcast(String message) {
        appMonManager.broadcast(message);
    }

}
