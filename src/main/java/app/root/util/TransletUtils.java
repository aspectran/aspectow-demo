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
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.web.support.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>Created: 2021/05/30</p>
 */
public class TransletUtils {

    public static String getRemoteAddr(@NonNull Translet translet) {
        String remoteAddr = translet.getRequestAdapter().getHeader(HttpHeaders.X_FORWARDED_FOR);
        if (StringUtils.hasLength(remoteAddr)) {
            if (remoteAddr.contains(",")) {
                remoteAddr = StringUtils.tokenize(remoteAddr, ",", true)[0];
            }
        } else {
            remoteAddr = ((HttpServletRequest)translet.getRequestAdaptee()).getRemoteAddr();
        }
        return remoteAddr;
    }

}
