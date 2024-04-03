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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogTailConfig extends AbstractParameters {

    private static final ParameterKey logtail;

    private static final ParameterKey[] parameterKeys;

    static {
        logtail = new ParameterKey("logtail", LogTailInfo.class, true, true);

        parameterKeys = new ParameterKey[] {
                logtail
        };
    }

    public LogTailConfig() {
        super(parameterKeys);
    }

    public LogTailConfig(String text) throws IOException {
        this();
        readFrom(text);
    }

    public LogTailConfig(File file) throws IOException {
        this();
        readFrom(file);
    }

    public LogTailConfig(Reader reader) throws IOException {
        this();
        readFrom(reader);
    }

    public List<LogTailInfo> getLogTailInfoList() {
        return getParametersList(logtail);
    }

    public List<MeasurementInfo> getMeasurementInfoList() {
        List<LogTailInfo> logTailInfoList = getLogTailInfoList();
        List<MeasurementInfo> measurementInfoList = new ArrayList<>(logTailInfoList.size());
        for (LogTailInfo logTailInfo : logTailInfoList) {
            MeasurementInfo measurementInfo = logTailInfo.getMeasurementInfo();
            if (measurementInfo != null) {
                measurementInfo.setGroup(logTailInfo.getGroup());
                measurementInfo.setName(logTailInfo.getName());
                measurementInfoList.add(measurementInfo);
            }
        }
        return measurementInfoList;
    }

}
