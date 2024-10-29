package app.root.appmon;

public interface AppMonEndpoint {

    void broadcast(String message);

    void broadcast(AppMonSession session, String message);

    boolean isUsingGroup(String group);

}
