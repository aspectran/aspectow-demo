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
package app.root.appmon.endpoint;

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class EndpointInfo extends AbstractParameters {

    private static final ParameterKey name;
    private static final ParameterKey title;
    private static final ParameterKey url;
    private static final ParameterKey pollingInterval;
    private static final ParameterKey resident;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        title = new ParameterKey("title", ValueType.STRING);
        url = new ParameterKey("url", ValueType.STRING);
        pollingInterval = new ParameterKey("pollingInterval", ValueType.INT);
        resident = new ParameterKey("resident", ValueType.BOOLEAN);

        parameterKeys = new ParameterKey[] {
                name,
                title,
                url,
                pollingInterval,
                resident
        };
    }

    public EndpointInfo() {
        super(parameterKeys);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(EndpointInfo.name, name);
    }

    public String getTitle() {
        return getString(title);
    }

    public void setTitle(String title) {
        putValue(EndpointInfo.title, title);
    }

    public String getUrl() {
        return getString(url);
    }

    public void setUrl(String url) {
        putValue(EndpointInfo.url, url);
    }

    public int getPollingInterval() {
        return getInt(pollingInterval, 0);
    }

    public void setPollingInterval(int pollingInterval) {
        putValue(EndpointInfo.pollingInterval, pollingInterval);
    }

    public boolean isResident() {
        return getBoolean(resident, false);
    }

    public void setResident(boolean resident) {
        putValue(EndpointInfo.resident, resident);
    }

}
