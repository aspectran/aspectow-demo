/*
 * Copyright (c) 2012-present-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.petclinic.common.aspect;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Aspect;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Before;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Joinpoint;
import com.aspectran.core.context.rule.type.MethodType;
import com.aspectran.utils.annotation.jsr305.NonNull;

@Component
@Bean
@Aspect("flashMapEchoAspect")
@Joinpoint(
        methods = {
                MethodType.GET
        },
        pointcut = {
                "+: /owners",
                "+: /owners/**"
        }
)
public class FlashMapEchoAspect {

    @Before
    public void before(@NonNull Translet translet) {
        if (translet.hasInputFlashMap()) {
            translet.setAttribute("flashMap", translet.getInputFlashMap());
        }
    }

}
