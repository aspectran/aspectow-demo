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
