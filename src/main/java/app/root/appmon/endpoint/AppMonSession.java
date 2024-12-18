package app.root.appmon.endpoint;

public interface AppMonSession {

    String[] getJoinedGroups();

    void saveJoinedGroups(String[] joinGroups);

    void removeJoinedGroups();

    boolean isTwoWay();

    boolean isValid();

}
