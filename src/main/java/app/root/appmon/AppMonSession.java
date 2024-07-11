package app.root.appmon;

public interface AppMonSession {

    String[] getJoinedGroups();

    void saveJoinedGroups(String[] joinGroups);

    void removeJoinedGroups();

    boolean isValid();

}
