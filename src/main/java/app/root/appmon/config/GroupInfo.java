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
package app.root.appmon.config;

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

import java.util.List;

/**
 * <p>Created: 2020/02/12</p>
 */
public class GroupInfo extends AbstractParameters {

    private static final ParameterKey name;
    private static final ParameterKey title;
    private static final ParameterKey event;
    private static final ParameterKey status;
    private static final ParameterKey logtail;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        title = new ParameterKey("title", ValueType.STRING);
        status = new ParameterKey("statuses", new String[] {"status"}, StatusInfo.class, true, true);
        event = new ParameterKey("events", new String[] {"event"}, EventInfo.class, true, true);
        logtail = new ParameterKey("logtails", new String[] {"logtail"}, LogtailInfo.class, true, true);

        parameterKeys = new ParameterKey[] {
                name,
                title,
                event,
                status,
                logtail
        };
    }

    public GroupInfo() {
        super(parameterKeys);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(GroupInfo.name, name);
    }

    public String getTitle() {
        return getString(title);
    }

    public void setTitle(String name) {
        putValue(GroupInfo.title, name);
    }

    public List<EventInfo> getEventInfoList() {
        return getParametersList(event);
    }

    public List<StatusInfo> getStatusInfoList() {
        return getParametersList(status);
    }

    public List<LogtailInfo> getLogtailInfoList() {
        return getParametersList(logtail);
    }

}
