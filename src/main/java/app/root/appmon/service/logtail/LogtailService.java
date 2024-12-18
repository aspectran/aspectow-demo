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
package app.root.appmon.service.logtail;

import app.root.appmon.config.LogtailInfo;
import app.root.appmon.service.Service;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.Parameters;
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

public class LogtailService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(LogtailService.class);

    private static final String LABEL_LOGTAIL = ":logtail:";

    private static final String LABEL_LAST = "last:";

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private static final int DEFAULT_SAMPLE_INTERVAL = 1000;

    private final LogtailServiceManager logtailServiceManager;

    private final LogtailInfo logtailInfo;

    private final String label;

    /** the Charset to be used for reading the file */
    private final Charset charset;

    /** how frequently to check for file changes; defaults to 1 second */
    private final int sampleInterval;

    private final int lastLines;

    /** the log file to tail */
    private final File logFile;

    private Tailer tailer;

    public LogtailService(@NonNull LogtailServiceManager logtailServiceManager,
                          @NonNull LogtailInfo logtailInfo,
                          @NonNull File logFile) {
        this.logtailServiceManager = logtailServiceManager;
        this.logtailInfo = logtailInfo;
        this.label = logtailInfo.getGroup() + ":" + logtailInfo.getName() + LABEL_LOGTAIL;
        this.charset = (logtailInfo.getCharset() != null ? Charset.forName(logtailInfo.getCharset()): DEFAULT_CHARSET);
        this.sampleInterval = (logtailInfo.getSampleInterval() > 0 ? logtailInfo.getSampleInterval() : DEFAULT_SAMPLE_INTERVAL);
        this.lastLines = logtailInfo.getLastLines();
        this.logFile = logFile;
    }

    @Override
    public String getName() {
        return logtailInfo.getName();
    }

    @SuppressWarnings("unchecked")
    public <V extends Parameters> V getServiceInfo() {
        return (V)logtailInfo;
    }

    public void read(@NonNull List<String> messages) {
        if (lastLines > 0) {
            try {
                if (logFile.exists()) {
                    List<String> lines = readLastLines(logFile, lastLines);
                    if (!lines.isEmpty()) {
                        messages.addAll(lines);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to read log file " + logFile, e);
            }
        }
    }

    @NonNull
    private List<String> readLastLines(File file, int lastLines) throws IOException {
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
                list.add(label + LABEL_LAST + line);
            }
            Collections.reverse(list);
        }
        return list;
    }

    @Override
    public void broadcast(String message) {
        logtailServiceManager.broadcast(label + message);
    }

    @Override
    protected void doStart() throws Exception {
        tailer = Tailer.builder()
                .setFile(logFile)
                .setTailerListener(new LogTailerListener(this))
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

    @Override
    public String toString() {
        if (isStopped()) {
            return ToStringBuilder.toString(super.toString(), logtailInfo);
        } else {
            return super.toString();
        }
    }

}
