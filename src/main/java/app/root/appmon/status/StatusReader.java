package app.root.appmon.status;

public interface StatusReader {

    void start();

    void stop();

    String read();

    String readIfChanged();

}
