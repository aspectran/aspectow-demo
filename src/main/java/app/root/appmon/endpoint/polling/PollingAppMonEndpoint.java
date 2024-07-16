package app.root.appmon.endpoint.polling;

import app.root.appmon.AppMonEndpoint;
import app.root.appmon.AppMonManager;
import app.root.appmon.endpoint.EndpointInfo;
import app.root.appmon.endpoint.EndpointPollingConfig;
import app.root.appmon.group.GroupInfo;
import app.root.appmon.logtail.LogtailInfo;
import app.root.appmon.status.StatusInfo;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Destroy;
import com.aspectran.core.component.bean.annotation.Initialize;
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

    private final PollingAppMonSessionManager pollingAppMonSessionManager;

    private final PollingAppMonBuffer buffer = new PollingAppMonBuffer();

    @Autowired
    public PollingAppMonEndpoint(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
        this.pollingAppMonSessionManager = new PollingAppMonSessionManager(appMonManager);
    }

    @Initialize
    public void initialize() throws Exception {
        appMonManager.putEndpoint(this);
        pollingAppMonSessionManager.initialize();
    }

    @Destroy
    public void destroy() throws Exception {
        pollingAppMonSessionManager.destroy();
    }

    @RequestToPost("/appmon/endpoint/join")
    @Transform(FormatType.JSON)
    public Map<String, Object> join(@NonNull Translet translet, String message) throws IOException {
        String sessionId = translet.getSessionAdapter().getId();

        EndpointInfo endpointInfo = appMonManager.getResidentEndpointInfo();
        EndpointPollingConfig pollingConfig = endpointInfo.getPollingConfig();

        PollingAppMonSession session = pollingAppMonSessionManager.createSession(sessionId, pollingConfig);
        if (!appMonManager.join(session)) {
            return null;
        }

        String[] joinGroups = StringUtils.splitCommaDelimitedString(message);
        List<GroupInfo> groups = appMonManager.getGroupInfoList(joinGroups);
        List<LogtailInfo> logtails = appMonManager.getLogtailInfoList(joinGroups);
        List<StatusInfo> statuses = appMonManager.getStatusInfoList(joinGroups);
        return Map.of(
                "groups", groups,
                "logtails", logtails,
                "statuses", statuses,
                "pollingInterval", session.getPollingInterval()
        );
    }

    @RequestToGet("/appmon/endpoint/pull")
    @Transform(FormatType.JSON)
    public String[] pull(@NonNull Translet translet) throws IOException {
        String sessionId = translet.getSessionAdapter().getId();
        PollingAppMonSession session = pollingAppMonSessionManager.getSession(sessionId);
        if (session == null || !session.isValid()) {
            return null;
        }

        String[] lines = buffer.pop(session);

        int minLineIndex = pollingAppMonSessionManager.getMinLineIndex();
        if (minLineIndex > -1) {
            buffer.remove(minLineIndex);
        }

        return (lines != null ? lines : new String[0]);
    }

    @RequestToPost("/appmon/endpoint/pollingInterval")
    @Transform(FormatType.TEXT)
    public int pollingInterval(@NonNull Translet translet, int speed) {
        String sessionId = translet.getSessionAdapter().getId();
        PollingAppMonSession session = pollingAppMonSessionManager.getSession(sessionId);
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
        buffer.push(line);
    }

    @Override
    public boolean isUsingGroup(String group) {
        return pollingAppMonSessionManager.isUsingGroup(group);
    }

}
