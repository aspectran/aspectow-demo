package app.root.appmon.service.status;

public interface StatusReader {

    void start();

    void stop();

    String read();

    String readIfChanged();

}
