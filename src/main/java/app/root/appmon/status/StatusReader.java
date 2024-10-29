package app.root.appmon.status;

public interface StatusReader {

    void init();

    String read();

    String readIfChanged();

}
