package app.root.appmon.group;

import app.root.appmon.endpoint.AppMonManager;
import com.aspectran.utils.Assert;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import jakarta.websocket.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupManager {

    private static final String JOINED_GROUPS_PROPERTY = "appmon/JoinedGroups";

    private final AppMonManager appMonManager;

    private final Map<String, GroupInfo> groups = new LinkedHashMap<>();

    public GroupManager(AppMonManager appMonManager, @NonNull List<GroupInfo> groupInfoList) {
        this.appMonManager = appMonManager;
        for (GroupInfo info : groupInfoList) {
            groups.put(info.getName(), info);
        }
    }

    public List<GroupInfo> getGroupInfoList(String[] joinGroups) {
        List<GroupInfo> infoList = new ArrayList<>(groups.size());
        if (joinGroups != null && joinGroups.length > 0) {
            for (String name : joinGroups) {
                for (GroupInfo info : groups.values()) {
                    if (info.getName().equals(name)) {
                        infoList.add(info);
                    }
                }
            }
        } else {
            infoList.addAll(groups.values());
        }
        return infoList;
    }

    public String[] getJoinedGroups(@NonNull Session session) {
        String[] savedGroups = (String[])session.getUserProperties().get(JOINED_GROUPS_PROPERTY);
        if (savedGroups == null) {
            return null;
        }
        Set<String> joinedGroups = new HashSet<>();
        for (String name : savedGroups) {
            if (groups.containsKey(name)) {
                joinedGroups.add(name);
            }
        }
        if (!joinedGroups.isEmpty()) {
            return joinedGroups.toArray(new String[0]);
        } else {
            return null;
        }
    }

    public String[] getUnusedGroups(Session session) {
        String[] joinedGroups = getJoinedGroups(session);
        if (joinedGroups == null || joinedGroups.length == 0) {
            return null;
        }
        List<String> unusedGroups = new ArrayList<>(joinedGroups.length);
        for (String name : joinedGroups) {
            if (!isUsingGroup(name)) {
                unusedGroups.add(name);
            }
        }
        if (!unusedGroups.isEmpty()) {
            return unusedGroups.toArray(new String[0]);
        } else {
            return null;
        }
    }

    public void saveJoinedGroups(@NonNull Session session, String[] joinGroups) {
        Assert.notEmpty(joinGroups, "joinGroups must not be null or empty");
        session.getUserProperties().put(JOINED_GROUPS_PROPERTY, joinGroups);
    }

    public void removeJoinedGroups(@NonNull Session session) {
        session.getUserProperties().remove(JOINED_GROUPS_PROPERTY);
    }

    public boolean isUsingGroup(String group) {
        if (StringUtils.hasLength(group)) {
            for (Session session : appMonManager.getEndpoint().getSessions()) {
                String[] savedGroups = (String[]) session.getUserProperties().get(JOINED_GROUPS_PROPERTY);
                if (savedGroups != null) {
                    for (String saved : savedGroups) {
                        if (group.equals(saved)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
