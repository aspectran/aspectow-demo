package app.root.appmon.config;

import com.aspectran.utils.annotation.jsr305.NonNull;
import jdk.jfr.Event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupInfoHolder {

    private final Map<String, GroupInfo> groupInfos = new LinkedHashMap<>();

    public GroupInfoHolder(@NonNull List<GroupInfo> groupInfoList) {
        for (GroupInfo info : groupInfoList) {
            groupInfos.put(info.getName(), info);

            for (EventInfo eventInfo : info.getEventInfoList()) {
                eventInfo.setGroup(info.getName());
            }
            for (StatusInfo statusInfo : info.getStatusInfoList()) {
                statusInfo.setGroup(info.getName());
            }
            for (LogtailInfo logtailInfo : info.getLogtailInfoList()) {
                logtailInfo.setGroup(info.getName());
            }
        }
    }

    public List<GroupInfo> getGroupInfoList(String[] joinGroups) {
        List<GroupInfo> infoList = new ArrayList<>(groupInfos.size());
        if (joinGroups != null && joinGroups.length > 0) {
            for (String name : joinGroups) {
                for (GroupInfo info : groupInfos.values()) {
                    if (info.getName().equals(name)) {
                        infoList.add(info);
                    }
                }
            }
        } else {
            infoList.addAll(groupInfos.values());
        }
        return infoList;
    }

    public boolean containsGroup(String groupName) {
        return groupInfos.containsKey(groupName);
    }

    @NonNull
    public static String[] extractGroupNames(@NonNull List<GroupInfo> groupInfoList) {
        List<String> groupNames = new ArrayList<>(groupInfoList.size());
        for (GroupInfo groupInfo : groupInfoList) {
            groupNames.add(groupInfo.getName());
        }
        return groupNames.toArray(new String[0]);
    }

}
