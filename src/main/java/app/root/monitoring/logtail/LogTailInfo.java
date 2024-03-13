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

import app.root.monitoring.measurement.MeasurementInfo;
import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogTailInfo extends AbstractParameters {

    private static final ParameterKey name;
    private static final ParameterKey title;
    private static final ParameterKey file;
    private static final ParameterKey charset;
    private static final ParameterKey sampleInterval;
    private static final ParameterKey lastLines;
    private static final ParameterKey visualizing;
    private static final ParameterKey measuring;
    private static final ParameterKey measurement;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        file = new ParameterKey("file", ValueType.STRING);
        title = new ParameterKey("title", ValueType.STRING);
        charset = new ParameterKey("charset", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.INT);
        lastLines = new ParameterKey("lastLines", ValueType.INT);
        visualizing = new ParameterKey("visualizing", ValueType.BOOLEAN);
        measuring = new ParameterKey("measuring", ValueType.BOOLEAN);
        measurement = new ParameterKey("measurement", MeasurementInfo.class);

        parameterKeys = new ParameterKey[] {
                name,
                title,
                file,
                charset,
                sampleInterval,
                lastLines,
                visualizing,
                measuring,
                measurement
        };
    }

    public LogTailInfo() {
        super(parameterKeys);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(LogTailInfo.name, name);
    }

    public String getTitle() {
        return getString(title);
    }

    public void setTitle(String title) {
        putValue(LogTailInfo.title, title);
    }

    public String getFile() {
        return getString(file);
    }

    public void setFile(String file) {
        putValue(LogTailInfo.file, file);
    }

    public String getCharset() {
        return getString(charset);
    }

    public void setCharset(String charset) {
        putValue(LogTailInfo.charset, charset);
    }

    public int getSampleInterval() {
        return getInt(sampleInterval, 0);
    }

    public void setSampleInterval(int sampleInterval) {
        putValue(LogTailInfo.sampleInterval, sampleInterval);
    }

    public int getLastLines() {
        return getInt(lastLines, 0);
    }

    public void setLastLines(int lastLines) {
        putValue(LogTailInfo.lastLines, lastLines);
    }

    public boolean isVisualizing() {
        return getBoolean(visualizing, false);
    }

    public void setVisualizing(boolean visualizing) {
        putValue(LogTailInfo.visualizing, visualizing);
    }

    public boolean isMeasuring() {
        return hasValue(measurement) && getBoolean(measuring, true);
    }

    public void setMeasuring(boolean measuring) {
        putValue(LogTailInfo.measuring, measuring);
    }

    public MeasurementInfo getMeasurementInfo() {
        return getParameters(measurement);
    }

    public void setMeasurementInfo(MeasurementInfo measurementInfo) {
        putValue(measurement, measurementInfo);
        if (measurementInfo != null && !hasValue(measuring)) {
            putValue(measuring, true);
        }
    }

}
