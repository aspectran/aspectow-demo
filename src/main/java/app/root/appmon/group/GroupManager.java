package app.root.appmon.group;

import com.aspectran.utils.annotation.jsr305.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupManager {

//    private final AppMonManager appMonManager;

    private final Map<String, GroupInfo> groups = new LinkedHashMap<>();

    public GroupManager(@NonNull List<GroupInfo> groupInfoList) {
//        this.appMonManager = appMonManager;
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

    public boolean containsGroup(String groupName) {
        return groups.containsKey(groupName);
    }

    @NonNull
    public static String[] extractGroupNames(@NonNull List<GroupInfo> groupInfoList) {
        List<String> groupNames = new ArrayList<>(groupInfoList.size());
        for (GroupInfo groupInfo : groupInfoList) {
            groupNames.add(groupInfo.getName());
        }
        return groupNames.toArray(new String[0]);
    }

//    public String[] getJoinedGroups(@NonNull AppMonSession session) {
//        String[] savedGroups = session.getJoinedGroups();
//        if (savedGroups == null) {
//            return null;
//        }
//        Set<String> joinedGroups = new HashSet<>();
//        for (String name : savedGroups) {
//            if (groups.containsKey(name)) {
//                joinedGroups.add(name);
//            }
//        }
//        if (!joinedGroups.isEmpty()) {
//            return joinedGroups.toArray(new String[0]);
//        } else {
//            return null;
//        }
//    }
//
//    public String[] getUnusedGroups(AppMonSession session) {
//        String[] joinedGroups = getJoinedGroups(session);
//        if (joinedGroups == null || joinedGroups.length == 0) {
//            return null;
//        }
//        List<String> unusedGroups = new ArrayList<>(joinedGroups.length);
//        for (String name : joinedGroups) {
//            if (!isUsingGroup(name)) {
//                unusedGroups.add(name);
//            }
//        }
//        if (!unusedGroups.isEmpty()) {
//            return unusedGroups.toArray(new String[0]);
//        } else {
//            return null;
//        }
//    }

//    public boolean isUsingGroup(String group) {
//        return appMonManager.getEndpoint().isUsingGroup(group);
//    }

}
