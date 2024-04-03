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

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class MeasurementInfo extends AbstractParameters {

    private static final ParameterKey group;
    private static final ParameterKey name;
    private static final ParameterKey sampleInterval;

    private static final ParameterKey[] parameterKeys;

    static {
        group = new ParameterKey("group", ValueType.STRING);
        name = new ParameterKey("name", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.INT);

        parameterKeys = new ParameterKey[] {
                group,
                name,
                sampleInterval
        };
    }

    public MeasurementInfo() {
        super(parameterKeys);
    }

    public String getGroup() {
        return getString(group);
    }

    public void setGroup(String group) {
        putValue(MeasurementInfo.group, group);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(MeasurementInfo.name, name);
    }

    public int getSampleInterval() {
        return getInt(sampleInterval, 0);
    }

    public void setSampleInterval(int sampleInterval) {
        putValue(MeasurementInfo.sampleInterval, sampleInterval);
    }

}
