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
package app.root.monitoring.appmon.logtail;

import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.io.input.Tailer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogTailer extends AbstractLifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(LogTailer.class);

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private static final int DEFAULT_SAMPLE_INTERVAL = 1000;

    private final LogtailManager manager;

    private final LogtailInfo info;

    private final String name;

    /** the Charset to be used for reading the file */
    private final Charset charset;

    /** how frequently to check for file changes; defaults to 1 second */
    private final int sampleInterval;

    private final int lastLines;

    /** the log file to tail */
    private final File logFile;

    private Tailer tailer;

    public LogTailer(LogtailManager manager, @NonNull LogtailInfo info, File logFile) {
        this.manager = manager;
        this.info = info;
        this.name = info.getName();
        this.charset = (info.getCharset() != null ? Charset.forName(info.getCharset()): DEFAULT_CHARSET);
        this.sampleInterval = (info.getSampleInterval() > 0 ? info.getSampleInterval() : DEFAULT_SAMPLE_INTERVAL);
        this.lastLines = info.getLastLines();
        this.logFile = logFile;
    }

    public LogtailInfo getInfo() {
        return info;
    }

    protected void readLastLines() {
        if (lastLines > 0) {
            try {
                if (logFile.exists()) {
                    String[] lines = readLastLines(logFile, lastLines);
                    for (String line : lines) {
                        manager.broadcast(name, "last:" + line);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to read log file " + logFile, e);
            }
        }
    }

    @NonNull
    private String[] readLastLines(File file, int lastLines) throws IOException {
        List<String> list = new ArrayList<>();
        try (ReversedLinesFileReader reversedLinesFileReader = ReversedLinesFileReader.builder()
                .setFile(file)
                .setCharset(charset)
                .get()) {
            int count = 0;
            while (count++ < lastLines) {
                String line = reversedLinesFileReader.readLine();
                if (line == null) {
                    break;
                }
                list.add(line);
            }
            Collections.reverse(list);
        }
        return list.toArray(new String[0]);
    }

    @Override
    protected void doStart() throws Exception {
        tailer = Tailer.builder()
                .setFile(logFile)
                .setTailerListener(new LogTailerListener(manager, name))
                .setDelayDuration(Duration.ofMillis(sampleInterval))
                .setTailFromEnd(true)
                .get();
    }

    @Override
    protected void doStop() throws Exception {
        if (tailer != null) {
            tailer.close();
            tailer = null;
        }
    }

}
