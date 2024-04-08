package app.root.appmon.endpoint;

import app.root.appmon.group.GroupInfo;
import app.root.appmon.group.GroupManager;
import app.root.appmon.group.GroupManagerBuilder;
import app.root.appmon.logtail.LogtailInfo;
import app.root.appmon.logtail.LogtailManager;
import app.root.appmon.logtail.LogtailManagerBuilder;
import app.root.appmon.measurement.MeasurementInfo;
import app.root.appmon.measurement.MeasurementManager;
import app.root.appmon.measurement.MeasurementManagerBuilder;
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

    private MeasurementManager measurementManager;

    public AppMonManager() {
    }

    @Initialize
    public void init(@Required AppMonEndpoint endpoint) throws Exception {
        Assert.state(this.endpoint == null, "AppMonManager is already initialized");
        this.endpoint = endpoint;
        this.endpointManager = EndpointManagerBuilder.build();
        this.groupManager = GroupManagerBuilder.build(this);
        this.logtailManager = LogtailManagerBuilder.build(this);
        this.measurementManager = MeasurementManagerBuilder.build(this);
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

    List<LogtailInfo> getLogTailInfoList(String[] joinGroups) {
        return logtailManager.getLogTailInfoList(joinGroups);
    }

    List<MeasurementInfo> getMeasurementInfoList(String[] joinGroups) {
        return measurementManager.getMeasurementInfoList(joinGroups);
    }

    synchronized void join(Session session) {
        String[] joinGroups = groupManager.getJoinedGroups(session);
        logtailManager.join(joinGroups);
        measurementManager.join(joinGroups);
        if (joinGroups != null) {
            groupManager.saveJoinedGroups(session, joinGroups);
        } else {
            groupManager.removeJoinedGroups(session);
        }
    }

    synchronized void release(Session session) {
        String[] unusedGroups = groupManager.getUnusedGroups(session);
        logtailManager.release(unusedGroups);
        measurementManager.release(unusedGroups);
        groupManager.removeJoinedGroups(session);
    }

    public String issueToken() {
        return TimeLimitedPBTokenIssuer.getToken();
    }

    public void validateToken(String token) throws InvalidPBTokenException {
        TimeLimitedPBTokenIssuer.validate(token);
    }

}
