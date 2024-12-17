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

import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.Parameters;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * <p>Created: 2024/12/17</p>
 */
public class AppMonConfig extends AbstractParameters {

    private static final ParameterKey group;
    private static final ParameterKey endpoint;

    private static final ParameterKey[] parameterKeys;

    static {
        endpoint = new ParameterKey("endpoint", EndpointInfo.class, true, true);
        group = new ParameterKey("group", GroupInfo.class, true, true);

        parameterKeys = new ParameterKey[] {
                endpoint,
                group
        };
    }

    public AppMonConfig() {
        super(parameterKeys);
    }

    public AppMonConfig(Reader reader) throws IOException {
        this();
        readFrom(reader);
    }

    public List<EndpointInfo> getEndpointInfoList() {
        return getParametersList(endpoint);
    }

    public List<GroupInfo> getGroupInfoList() {
        return getParametersList(group);
    }

    public List<StatusInfo> getStatusInfoList(String groupName) {
        Assert.notNull(groupName, "groupName must not be null");
        for (GroupInfo groupInfo : getGroupInfoList()) {
            if (groupName.equals(groupInfo.getName())) {
                return groupInfo.getStatusInfoList();
            }
        }
        return null;
    }

    public List<LogtailInfo> getLogtailInfoList(String groupName) {
        Assert.notNull(groupName, "groupName must not be null");
        for (GroupInfo groupInfo : getGroupInfoList()) {
            if (groupName.equals(groupInfo.getName())) {
                return groupInfo.getLogtailInfoList();
            }
        }
        return null;
    }

    private static void validateRequiredParameter(@NonNull Parameters parameters, ParameterKey key) {
        Assert.hasLength(parameters.getString(key),
                "Missing value of required parameter: " + parameters.getQualifiedName(key));
    }

}
