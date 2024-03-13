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

import app.root.monitoring.endpoint.MonitorEndpoint;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;
import jakarta.websocket.Session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogTailerManager {

    private static final Logger logger = LoggerFactory.getLogger(LogTailerManager.class);

    private static final String TAILERS_PROPERTY = "tailers";

    private final Map<String, LogTailer> tailers = new LinkedHashMap<>();

    private final MonitorEndpoint endpoint;

    public LogTailerManager(MonitorEndpoint endpoint, LogTailConfig logTailConfig) {
        this.endpoint = endpoint;
        addLogTailer(logTailConfig);
    }

    private void addLogTailer(LogTailConfig logTailConfig) {
        if (logTailConfig == null) {
            throw new IllegalArgumentException("logTailConfig must not be null");
        }
        List<LogTailInfo> tailerInfoList = logTailConfig.getLogTailInfoList();
        for (LogTailInfo logTailInfo : tailerInfoList) {
            File logFile = null;
            try {
                String file = endpoint.getActivityContext().getApplicationAdapter().toRealPath(logTailInfo.getFile());
                logFile = new File(file).getCanonicalFile();
            } catch (IOException e) {
                logger.error("Failed to resolve absolute path to log file " + logTailInfo.getFile(), e);
            }
            if (logFile != null) {
                LogTailer tailer = new LogTailer(this, logTailInfo, logFile);
                this.tailers.put(logTailInfo.getName(), tailer);
            }
        }
    }

    public List<LogTailInfo> getLogTailInfoList() {
        List<LogTailInfo> tailerInfoList = new ArrayList<>();
        for (LogTailer logTailer : tailers.values()) {
            tailerInfoList.add(logTailer.getInfo());
        }
        return tailerInfoList;
    }

    public synchronized void join(Session session, String[] names) {
        if (!tailers.isEmpty()) {
            if (names != null && names.length > 0) {
                List<String> list = new ArrayList<>();
                String[] existingNames = (String[])session.getUserProperties().get(TAILERS_PROPERTY);
                if (existingNames != null) {
                    Collections.addAll(list, existingNames);
                }
                for (String name : names) {
                    LogTailer tailer = tailers.get(name);
                    if (tailer != null) {
                        list.add(name);
                        tailer.readLastLines();
                        if (!tailer.isRunning()) {
                            try {
                                tailer.start();
                            } catch (Exception e) {
                                logger.warn(e);
                            }
                        }
                    }
                }
                session.getUserProperties().put(TAILERS_PROPERTY, list.toArray(new String[0]));
            } else {
                for (LogTailer tailer : tailers.values()) {
                    tailer.readLastLines();
                    if (!tailer.isRunning()) {
                        try {
                            tailer.start();
                        } catch (Exception e) {
                            logger.warn(e);
                        }
                    }
                }
            }
        }
    }

    public synchronized void release(Session session) {
        if (!tailers.isEmpty()) {
            String[] names = (String[])session.getUserProperties().get(TAILERS_PROPERTY);
            if (names != null) {
                for (String name : names) {
                    LogTailer tailer = tailers.get(name);
                    if (tailer != null && tailer.isRunning() && !isUsingTailer(name)) {
                        try {
                            tailer.stop();
                        } catch (Exception e) {
                            logger.warn(e);
                        }
                    }
                }
            } else {
                for (LogTailer tailer : tailers.values()) {
                    if (!tailer.isRunning()) {
                        try {
                            tailer.stop();
                        } catch (Exception e) {
                            logger.warn(e);
                        }
                    }
                }
            }
        }
    }

    private boolean isUsingTailer(String name) {
        for (Session session : endpoint.getSessions()) {
            String[] names = (String[])session.getUserProperties().get(TAILERS_PROPERTY);
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

    void broadcast(String name, String msg) {
        endpoint.broadcast(name+ ":" + msg);
    }

}
