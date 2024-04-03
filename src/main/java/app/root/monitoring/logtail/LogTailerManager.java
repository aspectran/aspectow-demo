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
package app.root.monitoring.logtail;

import app.root.monitoring.endpoint.MonitorManager;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogTailerManager {

    private static final Logger logger = LoggerFactory.getLogger(LogTailerManager.class);

    private final Map<String, LogTailer> tailers = new LinkedHashMap<>();

    private final MonitorManager monitorManager;

    public LogTailerManager(MonitorManager monitorManager) {
        this.monitorManager = monitorManager;
    }

    public void addLogTailer(String name, LogTailer tailer) {
        tailers.put(name, tailer);
    }

    public List<LogTailInfo> getLogTailInfoList() {
        List<LogTailInfo> tailerInfoList = new ArrayList<>();
        for (LogTailer logTailer : tailers.values()) {
            tailerInfoList.add(logTailer.getInfo());
        }
        return tailerInfoList;
    }

    public void join(String[] joinGroups) {
        if (!tailers.isEmpty()) {
            if (joinGroups != null && joinGroups.length > 0) {
                for (LogTailer tailer : tailers.values()) {
                    for (String group : joinGroups) {
                        if (tailer.getInfo().getGroup().equals(group)) {
                            start(tailer);
                        }
                    }
                }
            } else {
                for (LogTailer tailer : tailers.values()) {
                    start(tailer);
                }
            }
        }
    }

    private void start(LogTailer tailer) {
        tailer.readLastLines();
        if (!tailer.isRunning()) {
            try {
                tailer.start();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void release(String[] tailerGroups) {
        if (!tailers.isEmpty()) {
            if (tailerGroups != null) {
                for (LogTailer tailer : tailers.values()) {
                    for (String group : tailerGroups) {
                        if (tailer.getInfo().getGroup().equals(group) &&
                                tailer.isRunning() &&
                                !monitorManager.isUsingGroup(group)) {
                            stop(tailer);
                        }
                    }
                }
            } else {
                for (LogTailer tailer : tailers.values()) {
                    if (!tailer.isRunning()) {
                        stop(tailer);
                    }
                }
            }
        }
    }

    private void stop(LogTailer tailer) {
        try {
            tailer.stop();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    void broadcast(String name, String msg) {
        monitorManager.broadcast(name + ":" + msg);
    }

}
