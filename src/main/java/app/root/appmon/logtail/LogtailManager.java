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

import app.root.appmon.endpoint.AppMonManager;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogtailManager {

    private static final Logger logger = LoggerFactory.getLogger(LogtailManager.class);

    private final Map<String, LogTailer> tailers = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    public LogtailManager(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
    }

    public void addLogTailer(String name, LogTailer tailer) {
        tailers.put(name, tailer);
    }

    public List<LogtailInfo> getLogTailInfoList(String[] joinGroups) {
        List<LogtailInfo> infoList = new ArrayList<>(tailers.size());
        if (joinGroups != null && joinGroups.length > 0) {
            for (String name : joinGroups) {
                for (LogTailer logTailer : tailers.values()) {
                    if (logTailer.getInfo().getGroup().equals(name)) {
                        infoList.add(logTailer.getInfo());
                    }
                }
            }
        } else {
            for (LogTailer logTailer : tailers.values()) {
                infoList.add(logTailer.getInfo());
            }
        }
        return infoList;
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

    private void start(@NonNull LogTailer tailer) {
        tailer.readLastLines();
        if (!tailer.isRunning()) {
            try {
                tailer.start();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void release(String[] unusedGroups) {
        if (!tailers.isEmpty()) {
            if (unusedGroups != null) {
                for (LogTailer tailer : tailers.values()) {
                    for (String group : unusedGroups) {
                        if (tailer.getInfo().getGroup().equals(group) && tailer.isRunning()) {
                            stop(tailer);
                        }
                    }
                }
            } else {
                for (LogTailer tailer : tailers.values()) {
                    if (tailer.isRunning()) {
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
        appMonManager.getEndpoint().broadcast(name + ":" + msg);
    }

}
