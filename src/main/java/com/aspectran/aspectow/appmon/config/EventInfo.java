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
package com.aspectran.aspectow.appmon.config;

import com.aspectran.utils.Assert;
import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class EventInfo extends AbstractParameters {

    private static final ParameterKey group;
    private static final ParameterKey name;
    private static final ParameterKey reader;
    private static final ParameterKey target;
    private static final ParameterKey parameters;

    private static final ParameterKey[] parameterKeys;

    static {
        group = new ParameterKey("group", ValueType.STRING);
        name = new ParameterKey("name", ValueType.STRING);
        reader = new ParameterKey("reader", ValueType.STRING);
        target = new ParameterKey("target", ValueType.STRING);
        parameters = new ParameterKey("parameters", ValueType.PARAMETERS);

        parameterKeys = new ParameterKey[] {
                group,
                name,
                reader,
                target,
                parameters
        };
    }

    public EventInfo() {
        super(parameterKeys);
    }

    public String getGroup() {
        return getString(group);
    }

    void setGroup(String group) {
        putValue(EventInfo.group, group);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(EventInfo.name, name);
    }

    public String getReader() {
        return getString(reader);
    }

    public void setReader(String reader) {
        putValue(EventInfo.reader, reader);
    }

    public String getTarget() {
        return getString(target);
    }

    public void setTarget(String target) {
        putValue(EventInfo.target, target);
    }

    public boolean hasParameters() {
        return hasValue(parameters);
    }

    public Parameters getParameters() {
        return getParameters(parameters);
    }

    public void setParameters(Parameters parameters) {
        putValue(EventInfo.parameters, parameters);
    }

    public void validateRequiredParameters() {
        Assert.hasLength(getString(name), "Missing value of required parameter: " + getQualifiedName(name));
        Assert.hasLength(getString(reader), "Missing value of required parameter: " + getQualifiedName(reader));
        Assert.hasLength(getString(target), "Missing value of required parameter: " + getQualifiedName(target));
    }

}
