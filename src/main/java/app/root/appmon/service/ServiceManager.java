package app.root.appmon.service;

import app.root.appmon.manager.AppMonManager;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Created: 2024-12-18</p>
 */
public abstract class ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private final Map<String, Service> services = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    private final String groupName;

    public ServiceManager(AppMonManager appMonManager, String groupName) {
        this.appMonManager = appMonManager;
        this.groupName = groupName;
    }

    public AppMonManager getAppMonManager() {
        return appMonManager;
    }

    public String getGroupName() {
        return groupName;
    }

    @SuppressWarnings("unchecked")
    public <V extends Service> Iterator<V> getServices() {
        return (Iterator<V>)services.values().iterator();
    }

    public void addService(Service service) {
        services.put(service.getName(), service);
    }

    @SuppressWarnings("unchecked")
    public <V extends Service> V getService(String name) {
        Service service = services.get(name);
        if (service == null) {
            throw new IllegalArgumentException("No service found for name '" + name + "'");
        }
        return (V)service;
    }

    public void collectMessages(List<String> messages) {
        for (Service service : services.values()) {
            service.read(messages);
        }
    }

    public void start() {
        for (Service service : services.values()) {
            try {
                service.start();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void stop() {
        for (Service service : services.values()) {
            try {
                service.stop();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public <V> V getBean(@NonNull String id) {
        return appMonManager.getActivityContext().getBeanRegistry().getBean(id);
    }

    public <V> V getBean(Class<V> type) {
        return appMonManager.getActivityContext().getBeanRegistry().getBean(type);
    }

    public void broadcast(String message) {
        appMonManager.broadcast(message);
    }

}
