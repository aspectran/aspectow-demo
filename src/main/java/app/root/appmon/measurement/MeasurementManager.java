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
package app.root.appmon.measurement;

import app.root.appmon.endpoint.AppMonManager;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MeasurementManager {

    private static final Logger logger = LoggerFactory.getLogger(MeasurementManager.class);

    private final Map<String, MeasureService> measureServices = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    public MeasurementManager(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
    }

    void addMeasureService(String name, MeasureService measureService) {
        measureServices.put(name, measureService);
    }

    public List<MeasurementInfo> getMeasurementInfoList(String[] joinGroups) {
        List<MeasurementInfo> infoList = new ArrayList<>(measureServices.size());
        if (joinGroups != null && joinGroups.length > 0) {
            for (String name : joinGroups) {
                for (MeasureService service : measureServices.values()) {
                    if (service.getInfo().getGroup().equals(name)) {
                        infoList.add(service.getInfo());
                    }
                }
            }
        } else {
            for (MeasureService service : measureServices.values()) {
                infoList.add(service.getInfo());
            }
        }
        return infoList;
    }

    public void join(String[] joinGroups) {
        if (!measureServices.isEmpty()) {
            if (joinGroups != null && joinGroups.length > 0) {
                for (MeasureService service : measureServices.values()) {
                    for (String group : joinGroups) {
                        if (service.getGroup().equals(group)) {
                            start(service);
                        }
                    }
                }
            } else {
                for (MeasureService service : measureServices.values()) {
                    start(service);
                }
            }
        }
    }

    private void start(MeasureService service) {
        try {
            service.start();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public void release(String[] unusedGroups) {
        if (!measureServices.isEmpty()) {
            if (unusedGroups != null) {
                for (MeasureService service : measureServices.values()) {
                    for (String group : unusedGroups) {
                        if (service.getGroup().equals(group) && service.isRunning()) {
                            stop(service);
                        }
                    }
                }
            } else {
                for (MeasureService service : measureServices.values()) {
                    if (service.isRunning()) {
                        stop(service);
                    }
                }
            }
        }
    }

    private void stop(MeasureService service) {
        try {
            service.stop();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public ActivityContext getActivityContext() {
        return appMonManager.getActivityContext();
    }

    public <V> V getBean(@NonNull String id) {
        return getActivityContext().getBeanRegistry().getBean(id);
    }

    public void broadcast(String name, String msg) {
        appMonManager.getEndpoint().broadcast(name + ":" + msg);
    }

}
