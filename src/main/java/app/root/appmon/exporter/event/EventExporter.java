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
package app.root.appmon.exporter.event;

import app.root.appmon.config.EventInfo;
import app.root.appmon.exporter.Exporter;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.Parameters;

import java.util.List;

/**
 * <p>Created: 2024-12-18</p>
 */
public class EventExporter extends Exporter {

    private static final String TYPE = ":event:";

    private final EventExporterManager eventExporterManager;

    private final EventInfo eventInfo;

    private final EventReader eventReader;

    private final String label;

    public EventExporter(@NonNull EventExporterManager eventExporterManager,
                         @NonNull EventInfo eventInfo,
                         @NonNull EventReader eventReader) {
        this.eventExporterManager = eventExporterManager;
        this.eventInfo = eventInfo;
        this.eventReader = eventReader;
        this.label = eventInfo.getGroup() + TYPE + eventInfo.getName() + ":";
    }

    @Override
    public String getName() {
        return eventInfo.getName();
    }

    @SuppressWarnings("unchecked")
    public <V extends Parameters> V getExporterInfo() {
        return (V)eventInfo;
    }

    @Override
    public void read(@NonNull List<String> messages) {
        String json = eventReader.read();
        if (json != null) {
            messages.add(label + json);
        }
    }

    @Override
    public void broadcast(String message) {
        eventExporterManager.broadcast(label + message);
    }

    @Override
    protected void doStart() throws Exception {
        eventReader.start();
    }

    @Override
    protected void doStop() throws Exception {
        eventReader.stop();
    }

    @Override
    public String toString() {
        if (isStopped()) {
            return ToStringBuilder.toString(super.toString(), eventInfo);
        } else {
            return super.toString();
        }
    }

}
