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
package app.root.appmon.config;

import com.aspectran.utils.annotation.jsr305.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EndpointInfoHolder {

    private final Map<String, EndpointInfo> endpointInfos = new LinkedHashMap<>();

    public EndpointInfoHolder(@NonNull List<EndpointInfo> endpointInfoList) {
        for (EndpointInfo info : endpointInfoList) {
            endpointInfos.put(info.getName(), info);
        }
    }

    public List<EndpointInfo> getEndpointInfoList() {
        return new ArrayList<>(endpointInfos.values());
    }

    public EndpointInfo getResidentEndpointInfo() {
        for (EndpointInfo info : endpointInfos.values()) {
            if (info.isResident()) {
                return info;
            }
        }
        return null;
    }

}
