/*
 * Copyright (c) 2018-2025 The Aspectran Project
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
package com.aspectran.appmon.config;

import com.aspectran.utils.Assert;
import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class StateInfo extends AbstractParameters {

    private static final ParameterKey group;
    private static final ParameterKey name;
    private static final ParameterKey title;
    private static final ParameterKey reader;
    private static final ParameterKey target;
    private static final ParameterKey sampleInterval;

    private static final ParameterKey[] parameterKeys;

    static {
        group = new ParameterKey("group", ValueType.STRING);
        name = new ParameterKey("name", ValueType.STRING);
        title = new ParameterKey("title", ValueType.STRING);
        reader = new ParameterKey("reader", ValueType.STRING);
        target = new ParameterKey("target", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.INT);

        parameterKeys = new ParameterKey[] {
                group,
                name,
                title,
                reader,
                target,
                sampleInterval
        };
    }

    public StateInfo() {
        super(parameterKeys);
    }

    public String getGroup() {
        return getString(group);
    }

    void setGroup(String group) {
        putValue(StateInfo.group, group);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(StateInfo.name, name);
    }

    public String getTitle() {
        return getString(title);
    }

    public void setTitle(String title) {
        putValue(StateInfo.title, title);
    }

    public String getReader() {
        return getString(reader);
    }

    public void setReader(String reader) {
        putValue(StateInfo.reader, reader);
    }

    public String getTarget() {
        return getString(target);
    }

    public void setTarget(String target) {
        putValue(StateInfo.target, target);
    }

    public int getSampleInterval() {
        return getInt(sampleInterval, 0);
    }

    public void setSampleInterval(int sampleInterval) {
        putValue(StateInfo.sampleInterval, sampleInterval);
    }

    public void validateRequiredParameters() {
        Assert.hasLength(getString(name), "Missing value of required parameter: " + getQualifiedName(name));
        Assert.hasLength(getString(target), "Missing value of required parameter: " + getQualifiedName(target));
        Assert.hasLength(getString(reader), "Missing value of required parameter: " + getQualifiedName(reader));
    }

}
