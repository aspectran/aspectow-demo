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
package app.root.monitoring;

import app.root.monitoring.endpoint.EndpointConfig;
import app.root.monitoring.endpoint.EndpointInfo;
import com.aspectran.core.component.bean.annotation.Action;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.Required;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;
import com.aspectran.utils.security.InvalidPBTokenException;
import com.aspectran.utils.security.TimeLimitedPBTokenIssuer;
import com.aspectran.web.activity.response.DefaultRestResponse;
import com.aspectran.web.activity.response.RestResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>Created: 2020/02/23</p>
 */
@Component
public class MonitoringAction {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAction.class);

    private static final String ENDPOINT_CONFIG_FILE = "app/root/endpoint-config.apon";

    @Request("/monitoring/${endpoint}")
    @Dispatch("templates/default")
    @Action("page")
    public Map<String, String> viewer(String endpoint) {
        return Map.of(
                "headinclude", "monitoring/_endpoints",
                "include", "monitoring/monitor",
                "style", "fluid compact",
                "token", TimeLimitedPBTokenIssuer.getToken(),
                "endpoint", StringUtils.nullToEmpty(endpoint)
        );
    }

    @RequestToGet("/monitoring/endpoints/${token}")
    public RestResponse getEndpoints(@Required String token) throws IOException {
        try {
            TimeLimitedPBTokenIssuer.validate(token);
        } catch (InvalidPBTokenException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
            return new DefaultRestResponse().forbidden();
        }
        EndpointConfig endpointConfig = new EndpointConfig(ResourceUtils.getResourceAsReader(ENDPOINT_CONFIG_FILE));
        List<EndpointInfo> endpointInfoList = endpointConfig.getEndpointInfoList();
        for (EndpointInfo endpointInfo : endpointInfoList) {
            String url = endpointInfo.getUrl();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += TimeLimitedPBTokenIssuer.getToken();
            endpointInfo.setUrl(url);
        }
        return new DefaultRestResponse(endpointInfoList).ok();
    }

}
