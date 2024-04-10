package app.appmon.endpoint;

import app.appmon.group.GroupInfo;
import app.appmon.group.GroupManager;
import app.appmon.group.GroupManagerBuilder;
import app.appmon.logtail.LogtailInfo;
import app.appmon.logtail.LogtailManager;
import app.appmon.logtail.LogtailManagerBuilder;
import app.appmon.status.StatusInfo;
import app.appmon.status.StatusManager;
import app.appmon.status.StatusManagerBuilder;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Initialize;
import com.aspectran.core.component.bean.annotation.Required;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.security.InvalidPBTokenException;
import com.aspectran.utils.security.TimeLimitedPBTokenIssuer;
import jakarta.websocket.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created: 4/3/24</p>
 */
@Component
@Bean("AppMonManager")
public class AppMonManager extends InstantActivitySupport {

    private AppMonEndpoint endpoint;

    private EndpointManager endpointManager;

    private GroupManager groupManager;

    private LogtailManager logtailManager;

    private StatusManager statusManager;

    public AppMonManager() {
    }

    @Initialize
    public void init(@Required AppMonEndpoint endpoint) throws Exception {
        Assert.state(this.endpoint == null, "AppMonManager is already initialized");
        this.endpoint = endpoint;
        this.endpointManager = EndpointManagerBuilder.build();
        this.groupManager = GroupManagerBuilder.build(this);
        this.logtailManager = LogtailManagerBuilder.build(this);
        this.statusManager = StatusManagerBuilder.build(this);
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

    public AppMonEndpoint getEndpoint() {
        return endpoint;
    }

    public List<EndpointInfo> getEndpointInfoList(String token) {
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

    List<GroupInfo> getGroupInfoList(String[] joinGroups) {
        return groupManager.getGroupInfoList(joinGroups);
    }

    List<LogtailInfo> getLogtailInfoList(String[] joinGroups) {
        return logtailManager.getLogtailInfoList(joinGroups);
    }

    List<StatusInfo> getStatusInfoList(String[] joinGroups) {
        return statusManager.getStatusInfoList(joinGroups);
    }

    synchronized void join(Session session) {
        String[] joinGroups = groupManager.getJoinedGroups(session);
        logtailManager.join(joinGroups);
        statusManager.join(joinGroups);
        if (joinGroups != null) {
            groupManager.saveJoinedGroups(session, joinGroups);
        } else {
            groupManager.removeJoinedGroups(session);
        }
    }

    synchronized void release(Session session) {
        String[] unusedGroups = groupManager.getUnusedGroups(session);
        logtailManager.release(unusedGroups);
        statusManager.release(unusedGroups);
        groupManager.removeJoinedGroups(session);
    }

    public String issueToken() {
        return TimeLimitedPBTokenIssuer.getToken();
    }

    public void validateToken(String token) throws InvalidPBTokenException {
        TimeLimitedPBTokenIssuer.validate(token);
    }

}
