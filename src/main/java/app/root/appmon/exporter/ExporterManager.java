package app.root.appmon.exporter;

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
public abstract class ExporterManager {

    private static final Logger logger = LoggerFactory.getLogger(ExporterManager.class);

    private final Map<String, Exporter> exporters = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    private final String groupName;

    public ExporterManager(AppMonManager appMonManager, String groupName) {
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
    public <V extends Exporter> Iterator<V> getExporters() {
        return (Iterator<V>) exporters.values().iterator();
    }

    public void addExporter(Exporter exporter) {
        exporters.put(exporter.getName(), exporter);
    }

    @SuppressWarnings("unchecked")
    public <V extends Exporter> V getExporter(String name) {
        Exporter exporter = exporters.get(name);
        if (exporter == null) {
            throw new IllegalArgumentException("No exporter found for name '" + name + "'");
        }
        return (V)exporter;
    }

    public void collectMessages(List<String> messages) {
        for (Exporter exporter : exporters.values()) {
            exporter.read(messages);
        }
    }

    public void start() {
        for (Exporter exporter : exporters.values()) {
            try {
                exporter.start();
            } catch (Exception e) {
                logger.warn(e);
            }
        }
    }

    public void stop() {
        for (Exporter exporter : exporters.values()) {
            try {
                exporter.stop();
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
