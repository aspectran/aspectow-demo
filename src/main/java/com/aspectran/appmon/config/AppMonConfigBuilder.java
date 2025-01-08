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

import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.io.IOException;
import java.io.Reader;

public abstract class AppMonConfigBuilder {

    private static final String APPMON_CONFIG_FILE = "com/aspectran/appmon/appmon-config.apon";

    private static final String APPMON_CONFIG_FILE_PROD = "com/aspectran/appmon/appmon-config-prod.apon";

    @NonNull
    public static AppMonConfig build(boolean forProd) throws IOException {
        Reader reader;
        if (forProd) {
            reader = ResourceUtils.getResourceAsReader(APPMON_CONFIG_FILE_PROD);
        } else {
            reader = ResourceUtils.getResourceAsReader(APPMON_CONFIG_FILE);
        }
        return new AppMonConfig(reader);
    }

}
