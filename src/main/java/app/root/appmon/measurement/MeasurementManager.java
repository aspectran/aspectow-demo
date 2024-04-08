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

    private final Map<String, Measuring> measurings = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    public MeasurementManager(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
    }

    public void addMeasuring(String name, Measuring measuring) {
        measurings.put(name, measuring);
    }

    public List<MeasurementInfo> getMeasurementInfoList(String[] joinGroups) {
        List<MeasurementInfo> infoList = new ArrayList<>(measurings.size());
        if (joinGroups != null && joinGroups.length > 0) {
            for (String name : joinGroups) {
                for (Measuring measuring : measurings.values()) {
                    if (measuring.getInfo().getGroup().equals(name)) {
                        infoList.add(measuring.getInfo());
                    }
                }
            }
        } else {
            for (Measuring measuring : measurings.values()) {
                infoList.add(measuring.getInfo());
            }
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

    public void release(String[] unusedGroups) {
        if (!measurings.isEmpty()) {
            if (unusedGroups != null) {
                for (Measuring measuring : measurings.values()) {
                    for (String group : unusedGroups) {
                        if (measuring.getGroup().equals(group) && measuring.isRunning()) {
                            stop(measuring);
                        }
                    }
                }
            } else {
                for (Measuring measuring : measurings.values()) {
                    if (measuring.isRunning()) {
                        stop(measuring);
                    }
                }
            }
        }
    }

    private void stop(Measuring measuring) {
        try {
            measuring.stop();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    ActivityContext getActivityContext() {
        return appMonManager.getActivityContext();
    }

    <V> V getBean(@NonNull String id) {
        return getActivityContext().getBeanRegistry().getBean(id);
    }

    void broadcast(String name, String msg) {
        appMonManager.getEndpoint().broadcast(name + ":" + msg);
    }

}
