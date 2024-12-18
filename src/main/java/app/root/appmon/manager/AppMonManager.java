package app.root.appmon.manager;

import app.root.appmon.config.EndpointInfo;
import app.root.appmon.config.EndpointInfoHolder;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.GroupInfoHolder;
import app.root.appmon.config.LogtailInfo;
import app.root.appmon.config.StatusInfo;
import app.root.appmon.endpoint.AppMonEndpoint;
import app.root.appmon.endpoint.AppMonSession;
import app.root.appmon.service.event.EventServiceManager;
import app.root.appmon.service.logtail.LogtailService;
import app.root.appmon.service.logtail.LogtailServiceManager;
import app.root.appmon.service.status.StatusService;
import app.root.appmon.service.status.StatusServiceManager;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.annotation.jsr305.Nullable;
import com.aspectran.utils.security.InvalidPBTokenException;
import com.aspectran.utils.security.TimeLimitedPBTokenIssuer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>Created: 4/3/2024</p>
 */
public class AppMonManager extends InstantActivitySupport {

    private final EndpointInfoHolder endpointInfoHolder;

    private final GroupInfoHolder groupInfoHolder;

    private final List<StatusServiceManager> statusServiceManagers = new ArrayList<>();

    private final List<EventServiceManager> eventServiceManagers = new ArrayList<>();

    private final List<LogtailServiceManager> logtailServiceManagers = new ArrayList<>();

    private final List<AppMonEndpoint> endpoints = new ArrayList<>();

    public AppMonManager(
            EndpointInfoHolder endpointInfoHolder,
            GroupInfoHolder groupInfoHolder) {
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

    public void addServiceManager(StatusServiceManager statusServiceManager) {
        synchronized (statusServiceManagers) {
            if (!statusServiceManagers.contains(statusServiceManager)) {
                statusServiceManagers.add(statusServiceManager);
            }
        }
    }

    public void addServiceManager(EventServiceManager eventServiceManager) {
        synchronized (eventServiceManagers) {
            if (!eventServiceManagers.contains(eventServiceManager)) {
                eventServiceManagers.add(eventServiceManager);
            }
        }
    }

    public void addServiceManager(LogtailServiceManager logtailServiceManager) {
        synchronized (logtailServiceManagers) {
            if (!logtailServiceManagers.contains(logtailServiceManager)) {
                logtailServiceManagers.add(logtailServiceManager);
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

    public List<StatusInfo> getStatusInfoList(String[] joinGroups) {
        List<StatusInfo> statusInfoList = new ArrayList<>();
        if (joinGroups != null && joinGroups.length > 0) {
            for (String groupName : joinGroups) {
                for (StatusServiceManager statusServiceManager : statusServiceManagers) {
                    if (statusServiceManager.getGroupName().equals(groupName)) {
                        Iterator<StatusService> statusServices = statusServiceManager.getServices();
                        while (statusServices.hasNext()) {
                            statusInfoList.add(statusServices.next().getServiceInfo());
                        }
                    }
                }
            }
        } else {
            for (StatusServiceManager statusServiceManager : statusServiceManagers) {
                Iterator<StatusService> statusServices = statusServiceManager.getServices();
                while (statusServices.hasNext()) {
                    statusInfoList.add(statusServices.next().getServiceInfo());
                }
            }
        }
        return statusInfoList;
    }

    public List<LogtailInfo> getLogtailInfoList(String[] joinGroups) {
        List<LogtailInfo> logtailInfoList = new ArrayList<>();
        if (joinGroups != null && joinGroups.length > 0) {
            for (String groupName : joinGroups) {
                for (LogtailServiceManager logtailServiceManager : logtailServiceManagers) {
                    if (logtailServiceManager.getGroupName().equals(groupName)) {
                        Iterator<LogtailService> logtailServices = logtailServiceManager.getServices();
                        while (logtailServices.hasNext()) {
                            logtailInfoList.add(logtailServices.next().getServiceInfo());
                        }
                    }
                }
            }
        } else {
            for (LogtailServiceManager logtailServiceManager : logtailServiceManagers) {
                Iterator<LogtailService> logtailServices = logtailServiceManager.getServices();
                while (logtailServices.hasNext()) {
                    logtailInfoList.add(logtailServices.next().getServiceInfo());
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
                    for (StatusServiceManager statusServiceManager : statusServiceManagers) {
                        if (statusServiceManager.getGroupName().equals(group)) {
                            statusServiceManager.start();
                        }
                    }
                    for (LogtailServiceManager logtailServiceManager : logtailServiceManagers) {
                        if (logtailServiceManager.getGroupName().equals(group)) {
                            logtailServiceManager.start();
                        }
                    }
                }
            } else {
                for (StatusServiceManager statusServiceManager : statusServiceManagers) {
                    statusServiceManager.start();
                }
                for (LogtailServiceManager logtailServiceManager : logtailServiceManagers) {
                    logtailServiceManager.start();
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
                for (StatusServiceManager statusServiceManager : statusServiceManagers) {
                    if (statusServiceManager.getGroupName().equals(group)) {
                        statusServiceManager.stop();
                    }
                }
                for (LogtailServiceManager logtailServiceManager : logtailServiceManagers) {
                    if (logtailServiceManager.getGroupName().equals(group)) {
                        logtailServiceManager.stop();
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
                    for (StatusServiceManager statusServiceManager : statusServiceManagers) {
                        if (statusServiceManager.getGroupName().equals(group)) {
                            statusServiceManager.collectMessages(messages);
                        }
                    }
                    for (LogtailServiceManager logtailServiceManager : logtailServiceManagers) {
                        if (logtailServiceManager.getGroupName().equals(group)) {
                            logtailServiceManager.collectMessages(messages);
                        }
                    }
                }
            } else {
                for (StatusServiceManager statusServiceManager : statusServiceManagers) {
                    statusServiceManager.start();
                }
                for (LogtailServiceManager logtailServiceManager : logtailServiceManagers) {
                    logtailServiceManager.start();
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
