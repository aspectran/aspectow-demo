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
package app.root.monitoring.measurement;

import app.root.monitoring.endpoint.MonitorManager;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MeasuringManager {

    private static final Logger logger = LoggerFactory.getLogger(MeasuringManager.class);

    private final Map<String, Measuring> measurings = new LinkedHashMap<>();

    private final MonitorManager monitorManager;

    public MeasuringManager(MonitorManager monitorManager) {
        this.monitorManager = monitorManager;
    }

    public void addMeasuring(String name, Measuring measuring) {
        measurings.put(name, measuring);
    }

    public List<MeasurementInfo> getMeasurementInfoList(boolean detail) {
        List<MeasurementInfo> infoList = new ArrayList<>();
        for (Measuring measuring : measurings.values()) {
            MeasurementInfo info = new MeasurementInfo();
            info.setName(measuring.getName());
            if (detail) {
                info.setSampleInterval(measuring.getSampleInterval());
            }
            infoList.add(info);
        }
        return infoList;
    }

    public void join(String[] joinGroups) {
        if (!measurings.isEmpty()) {
            if (joinGroups != null && joinGroups.length > 0) {
                for (Measuring measuring : measurings.values()) {
                    for (String group : joinGroups) {
                        if (measuring.getGroup().equals(group)) {
                            start(measuring);
                        }
                    }
                }
            } else {
                for (Measuring measuring : measurings.values()) {
                    start(measuring);
                }
            }
        }
    }

    private void start(Measuring measuring) {
        try {
            measuring.start();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public void release(String[] tailerGroups) {
        if (!measurings.isEmpty()) {
            if (tailerGroups != null) {
                for (Measuring measuring : measurings.values()) {
                    for (String group : tailerGroups) {
                        if (measuring.getGroup().equals(group) &&
                                measuring.isRunning() &&
                                !monitorManager.isUsingGroup(group)) {
                            stop(measuring);
                        }
                    }
                }
            } else {
                for (Measuring measuring : measurings.values()) {
                    if (!measuring.isRunning()) {
                        stop(measuring);
                    }
                }
            }
        }
    }

    private void stop(Measuring measuring) {
        try {
            measuring.start();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    ActivityContext getActivityContext() {
        return monitorManager.getActivityContext();
    }

    void broadcast(String name, String msg) {
        monitorManager.broadcast(name + ":" + msg);
    }

}
