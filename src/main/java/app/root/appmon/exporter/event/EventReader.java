package app.root.appmon.exporter.event;

/**
 * <p>Created: 2024-12-18</p>
 */
public interface EventReader {

    void start() throws Exception;

    void stop();

}
