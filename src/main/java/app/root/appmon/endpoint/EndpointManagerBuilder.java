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

import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public abstract class EndpointManagerBuilder {

    private static final String ENDPOINT_CONFIG_FILE = "app/root/appmon/endpoint-config.apon";

    private static final String ENDPOINT_CONFIG_FILE_PROD = "app/root/appmon/endpoint-config-prod.apon";

    @NonNull
    public static EndpointManager build(boolean forProd) throws IOException {
        Reader reader;
        if (forProd) {
            reader = ResourceUtils.getResourceAsReader(ENDPOINT_CONFIG_FILE_PROD);
        } else {
            reader = ResourceUtils.getResourceAsReader(ENDPOINT_CONFIG_FILE);
        }
        EndpointConfig endpointConfig = new EndpointConfig(reader);
        List<EndpointInfo> endpointInfoList = endpointConfig.getEndpointInfoList();
        return new EndpointManager(endpointInfoList);
    }

}
