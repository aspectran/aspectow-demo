package app.root.appmon.exporter.state;

public interface StateReader {

    void start();

    void stop();

    String read();

    String readIfChanged();

}
