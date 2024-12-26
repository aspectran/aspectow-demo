/*
 * Copyright (c) 2018-2024 The Aspectran Project
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
package app.root.appmon.exporter.event;

import app.root.appmon.exporter.ExporterManager;
import app.root.appmon.manager.AppMonManager;

/**
 * <p>Created: 2024-12-18</p>
 */
public class EventExporterManager extends ExporterManager {

    public EventExporterManager(AppMonManager appMonManager, String groupName) {
        super(appMonManager, groupName);
    }

}
