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

import app.root.monitoring.endpoint.MonitorEndpoint;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;
import jakarta.websocket.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MeasuringManager {

    private static final Logger logger = LoggerFactory.getLogger(MeasuringManager.class);

    private static final String MEASUREMENTS_PROPERTY = "measurements";

    private final Map<String, Measuring> measurings = new LinkedHashMap<>();

    private final MonitorEndpoint endpoint;

    public MeasuringManager(MonitorEndpoint endpoint, List<MeasurementInfo> measurementInfoList) {
        this.endpoint = endpoint;
        addMeasuring(measurementInfoList);
    }

    private void addMeasuring(@NonNull List<MeasurementInfo> measurementInfoList) {
        for (MeasurementInfo measurementInfo : measurementInfoList) {
            Measuring measuring = new Measuring(this, measurementInfo);
            this.measurings.put(measuring.getName(), measuring);
        }
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

    public synchronized void join(Session session, String[] names) {
        if (!measurings.isEmpty()) {
            if (names != null && names.length > 0) {
                List<String> list = new ArrayList<>();
                String[] existingNames = (String[])session.getUserProperties().get(MEASUREMENTS_PROPERTY);
                if (existingNames != null) {
                    Collections.addAll(list, existingNames);
                }
                for (String name : names) {
                    Measuring measuring = measurings.get(name);
                    if (measuring != null) {
                        list.add(name);
                        if (!measuring.isRunning()) {
                            try {
                                measuring.start();
                            } catch (Exception e) {
                                logger.warn(e);
                            }
                        }
                        measuring.join();
                    }
                }
                session.getUserProperties().put(MEASUREMENTS_PROPERTY, list.toArray(new String[0]));
            } else {
                for (Measuring measuring : measurings.values()) {
                    if (!measuring.isRunning()) {
                        try {
                            measuring.start();
                        } catch (Exception e) {
                            logger.warn(e);
                        }
                    }
                    measuring.join();
                }
            }
        }
    }

    public synchronized void release(Session session) {
        if (!measurings.isEmpty()) {
            String[] names = (String[])session.getUserProperties().get(MEASUREMENTS_PROPERTY);
            if (names != null) {
                for (String name : names) {
                    Measuring measuring = measurings.get(name);
                    if (measuring != null && measuring.isRunning() && !isUsingMeasuring(name)) {
                        try {
                            measuring.stop();
                        } catch (Exception e) {
                            logger.warn(e);
                        }
                    }
                }
            } else {
                for (Measuring measuring : measurings.values()) {
                    if (!measuring.isRunning()) {
                        try {
                            measuring.stop();
                        } catch (Exception e) {
                            logger.warn(e);
                        }
                    }
                }
            }
        }
    }

    private boolean isUsingMeasuring(String name) {
        for (Session session : endpoint.getSessions()) {
            String[] names = (String[])session.getUserProperties().get(MEASUREMENTS_PROPERTY);
            if (names != null) {
                for (String name2 : names) {
                    if (name.equals(name2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    ActivityContext getActivityContext() {
        return endpoint.getActivityContext();
    }

    void broadcast(String name, String msg) {
        endpoint.broadcast(name+ ":" + msg);
    }

}
