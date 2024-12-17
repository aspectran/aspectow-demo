package app.root.appmon;

import app.root.appmon.config.EndpointInfo;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.GroupManager;
import app.root.appmon.config.LogtailInfo;
import app.root.appmon.config.StatusInfo;
import app.root.appmon.endpoint.EndpointManager;
import app.root.appmon.logtail.LogtailManager;
import app.root.appmon.logtail.LogtailService;
import app.root.appmon.status.StatusManager;
import app.root.appmon.status.StatusService;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.annotation.jsr305.Nullable;
import com.aspectran.utils.security.InvalidPBTokenException;
import com.aspectran.utils.security.TimeLimitedPBTokenIssuer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Created: 4/3/24</p>
 */
public class AppMonManager extends InstantActivitySupport {

    private final EndpointManager endpointManager;

    private final GroupManager groupManager;

    private final List<StatusManager> statusManagers = new ArrayList<>();

    private final List<LogtailManager> logtailManagers = new ArrayList<>();

    private final List<AppMonEndpoint> endpoints = new ArrayList<>();

    public AppMonManager(EndpointManager endpointManager,
                         GroupManager groupManager) {
        this.endpointManager = endpointManager;
        this.groupManager = groupManager;
    }

    @Override
    @NonNull
    public ActivityContext getActivityContext() {
        return super.getActivityContext();
    }

    @Override
    @NonNull
    public ApplicationAdapter getApplicationAdapter() {
        return super.getApplicationAdapter();
    }

    public void addStatusManager(StatusManager statusManager) {
        synchronized (statusManagers) {
            if (!statusManagers.contains(statusManager)) {
                statusManagers.add(statusManager);
            }
        }
    }

    public void addLogtailManager(LogtailManager logtailManager) {
        synchronized (logtailManagers) {
            if (!logtailManagers.contains(logtailManager)) {
                logtailManagers.add(logtailManager);
            }
        }
    }

    public void addEndpoint(AppMonEndpoint endpoint) {
        synchronized (endpoints) {
            if (!endpoints.contains(endpoint)) {
                endpoints.add(endpoint);
            }
        }
    }

    public EndpointInfo getResidentEndpointInfo() {
        EndpointInfo endpointInfo = endpointManager.getResidentEndpointInfo();
        if (endpointInfo == null) {
            throw new IllegalStateException("Resident EndpointInfo not found");
        }
        return endpointInfo.copy();
    }

    public List<EndpointInfo> getAvailableEndpointInfoList(String token) {
        List<EndpointInfo> endpointInfoList = new ArrayList<>();
        for (EndpointInfo endpointInfo : endpointManager.getEndpointInfoList()) {
            EndpointInfo info = endpointInfo.copy();
            String url = info.getUrl();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += token;
            info.setUrl(url);
            endpointInfoList.add(info);
        }
        return endpointInfoList;
    }

    public String[] getVerifiedGroupNames(String[] joinGroups) {
        List<GroupInfo> groups = getGroupInfoList(joinGroups);
        if (!groups.isEmpty()) {
            return GroupManager.extractGroupNames(groups);
        } else {
            return new String[0];
        }
    }

    public List<GroupInfo> getGroupInfoList(String[] joinGroups) {
        return groupManager.getGroupInfoList(joinGroups);
    }

    public List<StatusInfo> getStatusInfoList(String[] joinGroups) {
        List<StatusInfo> statusInfoList = new ArrayList<>();
        if (joinGroups != null && joinGroups.length > 0) {
            for (String groupName : joinGroups) {
                for (StatusManager statusManager : statusManagers) {
                    if (statusManager.getGroupName().equals(groupName)) {
                        for (StatusService statusService : statusManager.getStatusServices()) {
                            statusInfoList.add(statusService.getStatusInfo());
                        }
                    }
                }
            }
        } else {
            for (StatusManager statusManager : statusManagers) {
                for (StatusService statusService : statusManager.getStatusServices()) {
                    statusInfoList.add(statusService.getStatusInfo());
                }
            }
        }
        return statusInfoList;
    }

    public List<LogtailInfo> getLogtailInfoList(String[] joinGroups) {
        List<LogtailInfo> logtailInfoList = new ArrayList<>();
        if (joinGroups != null && joinGroups.length > 0) {
            for (String groupName : joinGroups) {
                for (LogtailManager logtailManager : logtailManagers) {
                    if (logtailManager.getGroupName().equals(groupName)) {
                        for (LogtailService logtailService : logtailManager.getLogtailServices()) {
                            logtailInfoList.add(logtailService.getLogtailInfo());
                        }
                    }
                }
            }
        } else {
            for (LogtailManager logtailManager : logtailManagers) {
                for (LogtailService logtailService : logtailManager.getLogtailServices()) {
                    logtailInfoList.add(logtailService.getLogtailInfo());
                }
            }
        }
        return logtailInfoList;
    }

    public synchronized boolean join(@NonNull AppMonSession session) {
        if (session.isValid()) {
            String[] joinGroups = session.getJoinedGroups();
            if (joinGroups != null && joinGroups.length > 0) {
                for (String group : joinGroups) {
                    for (StatusManager statusManager : statusManagers) {
                        if (statusManager.getGroupName().equals(group)) {
                            statusManager.start();
                        }
                    }
                    for (LogtailManager logtailManager : logtailManagers) {
                        if (logtailManager.getGroupName().equals(group)) {
                            logtailManager.start();
                        }
                    }
                }
            } else {
                for (StatusManager statusManager : statusManagers) {
                    statusManager.start();
                }
                for (LogtailManager logtailManager : logtailManagers) {
                    logtailManager.start();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public synchronized void release(AppMonSession session) {
        String[] unusedGroups = getUnusedGroups(session);
        if (unusedGroups != null) {
            for (String group : unusedGroups) {
                for (StatusManager statusManager : statusManagers) {
                    if (statusManager.getGroupName().equals(group)) {
                        statusManager.stop();
                    }
                }
                for (LogtailManager logtailManager : logtailManagers) {
                    if (logtailManager.getGroupName().equals(group)) {
                        logtailManager.stop();
                    }
                }
            }
        }
        session.removeJoinedGroups();
    }

    public List<String> getLastMessages(@NonNull AppMonSession session) {
        List<String> messages = new ArrayList<>();
        if (session.isValid()) {
            String[] joinGroups = session.getJoinedGroups();
            if (joinGroups != null && joinGroups.length > 0) {
                for (String group : joinGroups) {
                    for (StatusManager statusManager : statusManagers) {
                        if (statusManager.getGroupName().equals(group)) {
                            statusManager.collectStatuses(messages);
                        }
                    }
                    for (LogtailManager logtailManager : logtailManagers) {
                        if (logtailManager.getGroupName().equals(group)) {
                            logtailManager.collectLastLogs(messages);
                        }
                    }
                }
            } else {
                for (StatusManager statusManager : statusManagers) {
                    statusManager.start();
                }
                for (LogtailManager logtailManager : logtailManagers) {
                    logtailManager.start();
                }
            }
        }
        return messages;
    }

    public void broadcast(String message) {
        for (AppMonEndpoint endpoint : endpoints) {
            endpoint.broadcast(message);
        }
    }

    public void broadcast(AppMonSession session, String message) {
        for (AppMonEndpoint endpoint : endpoints) {
            endpoint.broadcast(session, message);
        }
    }

    @Nullable
    private String[] getUnusedGroups(AppMonSession session) {
        String[] joinedGroups = getJoinedGroups(session);
        if (joinedGroups == null || joinedGroups.length == 0) {
            return null;
        }
        List<String> unusedGroups = new ArrayList<>(joinedGroups.length);
        for (String name : joinedGroups) {
            boolean using = false;
            for (AppMonEndpoint endpoint : endpoints) {
                if (endpoint.isUsingGroup(name)) {
                    using = true;
                    break;
                }
            }
            if (!using) {
                unusedGroups.add(name);
            }
        }
        if (!unusedGroups.isEmpty()) {
            return unusedGroups.toArray(new String[0]);
        } else {
            return null;
        }
    }

    @Nullable
    private String[] getJoinedGroups(@NonNull AppMonSession session) {
        String[] savedGroups = session.getJoinedGroups();
        if (savedGroups == null) {
            return null;
        }
        Set<String> joinedGroups = new HashSet<>();
        for (String name : savedGroups) {
            if (groupManager.containsGroup(name)) {
                joinedGroups.add(name);
            }
        }
        if (!joinedGroups.isEmpty()) {
            return joinedGroups.toArray(new String[0]);
        } else {
            return null;
        }
    }

    public String issueToken() {
        return TimeLimitedPBTokenIssuer.getToken();
    }

    public void validateToken(String token) throws InvalidPBTokenException {
        TimeLimitedPBTokenIssuer.validate(token);
    }

}
