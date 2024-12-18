package app.root.appmon.service.event;

import app.root.appmon.manager.AppMonManager;
import app.root.appmon.service.ServiceManager;

/**
 * <p>Created: 2024-12-18</p>
 */
public class EventServiceManager extends ServiceManager {

    public EventServiceManager(AppMonManager appMonManager, String groupName) {
        super(appMonManager, groupName);
    }

}
