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
package app.root.appmon.exporter;

import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.lifecycle.AbstractLifeCycle;

import java.util.List;

/**
 * <p>Created: 2024-12-18</p>
 */
public abstract class Exporter extends AbstractLifeCycle {

    public abstract String getName();

    public abstract <V extends Parameters> V getExporterInfo();

    public abstract void read(List<String> messages);

    public abstract void broadcast(String message);

}
