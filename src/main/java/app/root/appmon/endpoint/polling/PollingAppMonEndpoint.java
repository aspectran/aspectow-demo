package app.root.appmon.endpoint.polling;

import app.root.appmon.AppMonEndpoint;
import app.root.appmon.AppMonManager;
import app.root.appmon.AppMonSession;
import app.root.appmon.config.EndpointInfo;
import app.root.appmon.config.EndpointPollingConfig;
import app.root.appmon.config.GroupInfo;
import app.root.appmon.config.LogtailInfo;
import app.root.appmon.config.StatusInfo;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Destroy;
import com.aspectran.core.component.bean.annotation.RequestToGet;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import com.aspectran.core.component.bean.annotation.Transform;
import com.aspectran.core.context.rule.type.FormatType;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class PollingAppMonEndpoint implements AppMonEndpoint {

    private final AppMonManager appMonManager;

    private final PollingAppMonService appMonService;

    @Autowired
    public PollingAppMonEndpoint(@NonNull AppMonManager appMonManager) throws Exception {
        this.appMonManager = appMonManager;

        EndpointInfo endpointInfo = appMonManager.getResidentEndpointInfo();
        EndpointPollingConfig pollingConfig = endpointInfo.getPollingConfig();
        if (pollingConfig != null && pollingConfig.isEnabled()) {
            this.appMonService = new PollingAppMonService(appMonManager, pollingConfig.getInitialBufferSize());
            this.appMonService.initialize();
            appMonManager.addEndpoint(this);
        } else {
            this.appMonService = null;
        }
    }

    @Destroy
    public void destroy() throws Exception {
        if (appMonService != null) {
            appMonService.destroy();
        }
    }

    @RequestToPost("/appmon/endpoint/join")
    @Transform(FormatType.JSON)
    public Map<String, Object> join(@NonNull Translet translet, String message) throws IOException {
        if (appMonService == null) {
            return null;
        }

        String sessionId = translet.getSessionAdapter().getId();

        EndpointInfo endpointInfo = appMonManager.getResidentEndpointInfo();
        EndpointPollingConfig pollingConfig = endpointInfo.getPollingConfig();
        String[] joinGroups = StringUtils.splitCommaDelimitedString(message);

        PollingAppMonSession appMonSession = appMonService.createSession(sessionId, pollingConfig, joinGroups);
        if (!appMonManager.join(appMonSession)) {
            return null;
        }

        List<GroupInfo> groups = appMonManager.getGroupInfoList(appMonSession.getJoinedGroups());
        List<LogtailInfo> logtails = appMonManager.getLogtailInfoList(appMonSession.getJoinedGroups());
        List<StatusInfo> statuses = appMonManager.getStatusInfoList(appMonSession.getJoinedGroups());
        List<String> messages = appMonManager.getLastMessages(appMonSession);
        return Map.of(
                "groups", groups,
                "statuses", statuses,
                "logtails", logtails,
                "pollingInterval", appMonSession.getPollingInterval(),
                "messages", messages
        );
    }

    @RequestToGet("/appmon/endpoint/pull")
    @Transform(FormatType.JSON)
    public String[] pull(@NonNull Translet translet) throws IOException {
        if (appMonService == null) {
            return null;
        }

        String sessionId = translet.getSessionAdapter().getId();
        PollingAppMonSession session = appMonService.getSession(sessionId);
        if (session == null || !session.isValid()) {
            return null;
        }

        String[] lines = appMonService.pull(session);
        return (lines != null ? lines : new String[0]);
    }

    @RequestToPost("/appmon/endpoint/pollingInterval")
    @Transform(FormatType.TEXT)
    public int pollingInterval(@NonNull Translet translet, int speed) {
        if (appMonService == null) {
            return -1;
        }

        String sessionId = translet.getSessionAdapter().getId();
        PollingAppMonSession session = appMonService.getSession(sessionId);
        if (session == null) {
            return -1;
        }

        if (speed == 1) {
            session.setPollingInterval(1000);
        } else {
            EndpointInfo endpointInfo = appMonManager.getResidentEndpointInfo();
            EndpointPollingConfig pollingConfig = endpointInfo.getPollingConfig();
            session.setPollingInterval(pollingConfig.getPollingInterval());
        }

        return session.getPollingInterval();
    }

    @Override
    public void broadcast(String line) {
        if (appMonService != null) {
            appMonService.push(line);
        }
    }

    @Override
    public void broadcast(AppMonSession session, String message) {
    }

    @Override
    public boolean isUsingGroup(String group) {
        if (appMonService != null) {
            return appMonService.isUsingGroup(group);
        } else {
            return false;
        }
    }

}
