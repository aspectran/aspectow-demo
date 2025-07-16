/*
 * Copyright (c) 2018-present The Aspectran Project
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
package app.root.common;

import app.root.util.IPv6Util;
import app.root.util.TransletUtils;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.ablility.DisposableBean;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.utils.Assert;
import com.aspectran.utils.SystemUtils;
import com.aspectran.utils.apon.JsonToParameters;
import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.cache.Cache;
import com.aspectran.utils.cache.ConcurrentLruCache;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * WHOIS OpenAPI
 *
 * <p>{"whois":{"query":"185.80.140.175","queryType":"IPv4","registry":"RIPENCC","countryCode":"YE"}}</p>
 *
 * <p>Created: 2020/06/29</p>
 */
@Component
@Bean("ipToCountryLookup")
public class IPToCountryLookup implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(IPToCountryLookup.class);

    private static final int TIMEOUT = 3000;

    private static final String NONE = "(none)";

    private static final String FAILED = "(failed)";

    private static final List<String> iso2CountryCodes;

    private static final List<String> lookupAvoidedList;

    private static final String apiUrl;

    private final Cache<String, String> cache =
            new ConcurrentLruCache<>(128, this::getCountryCode);

    private final CloseableHttpClient httpClient;

    static {
        iso2CountryCodes = List.of(Locale.getISOCountries());

        lookupAvoidedList = List.of(
                "127.0.0.1",
                "0000:0000:0000:0000:0000:0000:0000:0001",
                "localhost"
        );

        apiUrl = SystemUtils.getProperty("ipascc.api.url");
    }

    public IPToCountryLookup() {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(TIMEOUT))
                .setSocketTimeout(Timeout.ofMilliseconds(TIMEOUT))
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(TIMEOUT))
                .setResponseTimeout(Timeout.ofMilliseconds(TIMEOUT))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(5);
        connectionManager.setMaxTotal(5);
        connectionManager.setDefaultConnectionConfig(connectionConfig);
        connectionManager.getTotalStats();

        httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Override
    public void destroy() throws Exception {
        httpClient.close();
        cache.clear();
    }

    public String getCountryCode(Translet translet) {
        String remoteAddr = TransletUtils.getRemoteAddr(translet);
        return getCountryCode(remoteAddr, translet.getRequestAdapter().getLocale());
    }

    public String getCountryCode(String ipAddress, Locale locale) {
        Assert.notNull(ipAddress, "ipAddress must not be null");

        String ip6 = IPv6Util.normalize(ipAddress);
        if (ip6 != null) {
            ipAddress = ip6;
        }

        if (apiUrl == null ||
                lookupAvoidedList.contains(ipAddress) ||
                ipAddress.startsWith("192.168.0.") ||
                ipAddress.startsWith("10.")) {
            return getCountryCode(locale);
        }

        String countryCode = cache.get(ipAddress);
        if (countryCode == null || NONE.equals(countryCode) || FAILED.equals(countryCode)) {
            countryCode = getCountryCode(locale);
        }
        return countryCode;
    }

    private String getCountryCode(String ipAddress) {
        ClassicRequestBuilder requestBuilder = ClassicRequestBuilder
                .get()
                .setCharset(StandardCharsets.UTF_8)
                .setUri(apiUrl + ipAddress);

        ClassicHttpRequest request = requestBuilder.build();

        try {
            return httpClient.execute(request, response -> {
                        int statusCode = response.getCode();
                        if (statusCode != 200) {
                            throw new IOException("Failed with HTTP error code : " + statusCode);
                        }
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            String result = EntityUtils.toString(entity);
                            Parameters parameters = JsonToParameters.from(result);
                            Parameters whois = parameters.getParameters("whois");
                            String countryCode = whois.getString("countryCode");
                            if (countryCode == null || !iso2CountryCodes.contains(countryCode)) {
                                countryCode = NONE;
                            }
                            if (logger.isDebugEnabled()) {
                                logger.debug("Country code of IP address {} is {}", ipAddress, countryCode);
                            }
                            return countryCode;
                        }
                        return FAILED;
                    });
        } catch (IOException e) {
            logger.error("IP address lookup failed: {}", ipAddress, e);
            return FAILED;
        }
    }

    private String getCountryCode(Locale locale) {
        return (locale != null ? locale.getCountry() : null);
    }

    public static void main(String[] args) throws Exception {
        IPToCountryLookup ipToCountryLookup = new IPToCountryLookup();
        System.out.println(ipToCountryLookup.getCountryCode("103.99.216.86", Locale.KOREA));
        System.out.println(ipToCountryLookup.getCountryCode("103.99.216.999", Locale.KOREA));
        System.out.println(ipToCountryLookup.getCountryCode("0:0:0:0:0:0:0:1", Locale.KOREA));
        System.out.println(ipToCountryLookup.getCountryCode("2a01:6502:a56:4735::1", Locale.KOREA));
        ipToCountryLookup.destroy();
    }

}
