/*
 * Copyright (c) 2019-2024 The Aspectran Project
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
package app.root.util;

import com.aspectran.core.activity.Translet;
import com.aspectran.utils.Assert;
import com.aspectran.utils.SystemUtils;
import com.aspectran.utils.apon.JsonToParameters;
import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.cache.Cache;
import com.aspectran.utils.cache.ConcurrentLruCache;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * WHOIS OpenAPI
 *
 * <p>{"whois":{"query":"185.80.140.175","queryType":"IPv4","registry":"RIPENCC","countryCode":"YE"}}</p>
 *
 * <p>Created: 2020/06/29</p>
 */
public class CountryCodeLookup {

    private static final Logger logger = LoggerFactory.getLogger(CountryCodeLookup.class);

    private static final int TIMEOUT = 3000;

    private static final String NONE = "(none)";

    private static final String FAILED = "(failed)";

    private static final Cache<String, String> cache =
            new ConcurrentLruCache<>(64, CountryCodeLookup::getCountryCode);

    private static final List<String> iso2CountryCodes;

    private static final List<String> lookupAvoidedList;

    private static final String apiUrl;

    private static final CloseableHttpClient httpClient;

    private static final RequestConfig requestConfig;

    private static final CountryCodeLookup instance;

    static {
        iso2CountryCodes = List.of(Locale.getISOCountries());

        lookupAvoidedList = List.of(
                "127.0.0.1",
                "0000:0000:0000:0000:0000:0000:0000:0001",
                "localhost"
        );

        apiUrl = SystemUtils.getProperty("ipascc.api.url");

        httpClient = HttpClients.createDefault();
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(TIMEOUT)
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .build();

        instance = new CountryCodeLookup();
    }

    private CountryCodeLookup() {
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

    private static String getCountryCode(String ipAddress) {
        HttpGet request = new HttpGet(apiUrl + ipAddress);
        request.setConfig(requestConfig);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new IOException("Failed with HTTP error code : " + statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                Parameters parameters = JsonToParameters.from(result);
                Parameters whois = parameters.getParameters("whois");
                String countryCode = whois.getString("countryCode");
                if (countryCode != null && iso2CountryCodes.contains(countryCode)) {
                    return countryCode;
                } else {
                    return NONE;
                }
            }
        } catch (IOException e) {
            logger.error("IP address lookup failed: " + ipAddress, e);
        }
        return FAILED;
    }

    private String getCountryCode(Locale locale) {
        return (locale != null ? locale.getCountry() : null);
    }

    public static CountryCodeLookup getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(getInstance().getCountryCode("103.99.216.86", Locale.KOREA));
        System.out.println(getInstance().getCountryCode("103.99.216.999", Locale.KOREA));
        System.out.println(getInstance().getCountryCode("0:0:0:0:0:0:0:1", Locale.KOREA));
        System.out.println(getInstance().getCountryCode("2a01:6502:a56:4735::1", Locale.KOREA));
    }

}
