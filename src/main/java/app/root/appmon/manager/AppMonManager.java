package app.root.appmon.manager;

import app.root.appmon.config.EndpointInfo;
import app.root.appmon.config.EndpointInfoHolder;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.GroupInfoHolder;
import app.root.appmon.endpoint.AppMonEndpoint;
import app.root.appmon.endpoint.AppMonSession;
import app.root.appmon.exporter.event.EventExporterManager;
import app.root.appmon.exporter.log.LogExporterManager;
import app.root.appmon.exporter.state.StateExporterManager;
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
 * <p>Created: 4/3/2024</p>
 */
public class AppMonManager extends InstantActivitySupport {

    private final EndpointInfoHolder endpointInfoHolder;

    private final GroupInfoHolder groupInfoHolder;

    private final List<StateExporterManager> stateExporterManagers = new ArrayList<>();

    private final List<EventExporterManager> eventExporterManagers = new ArrayList<>();

    private final List<LogExporterManager> logExporterManagers = new ArrayList<>();

    private final Set<AppMonEndpoint> endpoints = new HashSet<>();

    public AppMonManager(EndpointInfoHolder endpointInfoHolder, GroupInfoHolder groupInfoHolder) {
        this.endpointInfoHolder = endpointInfoHolder;
        this.groupInfoHolder = groupInfoHolder;
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

    EventExporterManager newEventExporterManager(String groupName) {
        EventExporterManager eventExporterManager = new EventExporterManager(this, groupName);
        eventExporterManagers.add(eventExporterManager);
        return eventExporterManager;
    }

    StateExporterManager newStateExporterManager(String groupName) {
        StateExporterManager stateExporterManager = new StateExporterManager(this, groupName);
        stateExporterManagers.add(stateExporterManager);
        return stateExporterManager;
    }

    LogExporterManager newLogExporterManager(String groupName) {
        LogExporterManager logExporterManager = new LogExporterManager(this, groupName);
        logExporterManagers.add(logExporterManager);
        return logExporterManager;
    }

    public void addEndpoint(AppMonEndpoint endpoint) {
        endpoints.add(endpoint);
    }

    public EndpointInfo getResidentEndpointInfo() {
        EndpointInfo endpointInfo = endpointInfoHolder.getResidentEndpointInfo();
        if (endpointInfo == null) {
            throw new IllegalStateException("Resident EndpointInfo not found");
        }
        return endpointInfo.copy();
    }

    public List<EndpointInfo> getAvailableEndpointInfoList(String token) {
        List<EndpointInfo> endpointInfoList = new ArrayList<>();
        for (EndpointInfo endpointInfo : endpointInfoHolder.getEndpointInfoList()) {
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
            return GroupInfoHolder.extractGroupNames(groups);
        } else {
            return new String[0];
        }
    }

    public List<GroupInfo> getGroupInfoList(String[] joinGroups) {
        return groupInfoHolder.getGroupInfoList(joinGroups);
    }

    public synchronized boolean join(@NonNull AppMonSession session) {
        if (session.isValid()) {
            String[] joinGroups = session.getJoinedGroups();
            if (joinGroups != null && joinGroups.length > 0) {
                for (String group : joinGroups) {
                    for (EventExporterManager eventExporterManager : eventExporterManagers) {
                        if (eventExporterManager.getGroupName().equals(group)) {
                            eventExporterManager.start();
                        }
                    }
                    for (StateExporterManager stateExporterManager : stateExporterManagers) {
                        if (stateExporterManager.getGroupName().equals(group)) {
                            stateExporterManager.start();
                        }
                    }
                    for (LogExporterManager logExporterManager : logExporterManagers) {
                        if (logExporterManager.getGroupName().equals(group)) {
                            logExporterManager.start();
                        }
                    }
                }
            } else {
                for (EventExporterManager eventExporterManager : eventExporterManagers) {
                    eventExporterManager.start();
                }
                for (StateExporterManager stateExporterManager : stateExporterManagers) {
                    stateExporterManager.start();
                }
                for (LogExporterManager logExporterManager : logExporterManagers) {
                    logExporterManager.start();
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
                for (EventExporterManager eventExporterManager : eventExporterManagers) {
                    if (eventExporterManager.getGroupName().equals(group)) {
                        eventExporterManager.stop();
                    }
                }
                for (StateExporterManager stateExporterManager : stateExporterManagers) {
                    if (stateExporterManager.getGroupName().equals(group)) {
                        stateExporterManager.stop();
                    }
                }
                for (LogExporterManager logExporterManager : logExporterManagers) {
                    if (logExporterManager.getGroupName().equals(group)) {
                        logExporterManager.stop();
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
                    for (EventExporterManager eventExporterManager : eventExporterManagers) {
                        if (eventExporterManager.getGroupName().equals(group)) {
                            eventExporterManager.collectMessages(messages);
                        }
                    }
                    for (StateExporterManager stateExporterManager : stateExporterManagers) {
                        if (stateExporterManager.getGroupName().equals(group)) {
                            stateExporterManager.collectMessages(messages);
                        }
                    }
                    for (LogExporterManager logExporterManager : logExporterManagers) {
                        if (logExporterManager.getGroupName().equals(group)) {
                            logExporterManager.collectMessages(messages);
                        }
                    }
                }
            } else {
                for (EventExporterManager eventExporterManager : eventExporterManagers) {
                    eventExporterManager.collectMessages(messages);
                }
                for (StateExporterManager stateExporterManager : stateExporterManagers) {
                    stateExporterManager.collectMessages(messages);
                }
                for (LogExporterManager logExporterManager : logExporterManagers) {
                    logExporterManager.collectMessages(messages);
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
            if (groupInfoHolder.containsGroup(name)) {
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
