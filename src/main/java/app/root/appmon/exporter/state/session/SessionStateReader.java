package app.root.appmon.exporter.state.session;

import app.jpetstore.user.UserSession;
import app.root.appmon.config.StateInfo;
import app.root.appmon.exporter.state.StateExporter;
import app.root.appmon.exporter.state.StateExporterManager;
import app.root.appmon.exporter.state.StateReader;
import com.aspectran.core.component.session.DefaultSession;
import com.aspectran.core.component.session.Session;
import com.aspectran.core.component.session.SessionHandler;
import com.aspectran.core.component.session.SessionListenerRegistration;
import com.aspectran.core.component.session.SessionStatistics;
import com.aspectran.undertow.server.TowServer;
import com.aspectran.utils.Assert;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.json.JsonBuilder;
import com.aspectran.utils.json.JsonString;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SessionStateReader implements StateReader {

    private static final Logger logger = LoggerFactory.getLogger(SessionStateReader.class);

    public static final String USER_SESSION_KEY = "user";

    private final StateExporterManager stateExporterManager;

    private final StateInfo stateInfo;

    private final String deploymentName;

    private final SessionHandler sessionHandler;

    private SessionStateListener sessionListener;

    private volatile SessionStatePayload oldPayload;

    public SessionStateReader(@NonNull StateExporterManager stateExporterManager,
                              @NonNull StateInfo stateInfo) {
        this.stateExporterManager = stateExporterManager;
        this.stateInfo = stateInfo;

        String[] arr = StringUtils.split(stateInfo.getTarget(), '/', 2);
        String serverId = arr[0];
        String deploymentName = arr[1];

        try {
            TowServer towServer = stateExporterManager.getBean(serverId);
            this.sessionHandler = towServer.getSessionHandler(deploymentName);
            this.deploymentName = deploymentName;
        } catch (Exception e) {
            throw new RuntimeException("Cannot resolve session handler with " + stateInfo.getTarget(), e);
        }
    }

    public StateExporter getStateExporter() {
        return stateExporterManager.getExporter(stateInfo.getName());
    }

    @Override
    public void start() {
        sessionListener = new SessionStateListener(this);
        getSessionListenerRegistration().register(sessionListener, deploymentName);
    }

    @Override
    public void stop() {
        oldPayload = null;
        if (sessionListener != null) {
            getSessionListenerRegistration().remove(sessionListener, deploymentName);
        }
    }

    @NonNull
    private SessionListenerRegistration getSessionListenerRegistration() {
        SessionListenerRegistration sessionListenerRegistration = stateExporterManager.getBean(SessionListenerRegistration.class);
        if (sessionListenerRegistration == null) {
            throw new IllegalStateException("Bean for SessionListenerRegistration must be defined");
        }
        return sessionListenerRegistration;
    }

    @Override
    public String read() {
        try {
            SessionStatePayload payload = loadWithActiveSessions();
            oldPayload = payload;
            return payload.toJson();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public String readIfChanged() {
        try {
            SessionStatePayload payload = load();
            if (!payload.equals(oldPayload)) {
                oldPayload = payload;
                return payload.toJson();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    String readWithCreatedSession(Session session) {
        SessionStatePayload payload = load();
        oldPayload = payload;
        payload.setCreatedSessions(new JsonString[] { serialize(session) });
        return payload.toJson();
    }

    String readWithDestroyedSession(String sessionId) {
        SessionStatePayload payload = load();
        oldPayload = payload;
        payload.setDestroyedSessions(new String[] { sessionId });
        return payload.toJson();
    }

    SessionStatePayload loadWithActiveSessions() {
        SessionStatePayload payload = load();
        payload.setCreatedSessions(getAllActiveSessions());
        return payload;
    }

    @NonNull
    private SessionStatePayload load() {
        SessionStatistics statistics = sessionHandler.getStatistics();
        SessionStatePayload payload = new SessionStatePayload();
        payload.setCreatedSessionCount(statistics.getCreatedSessions());
        payload.setExpiredSessionCount(statistics.getExpiredSessions());
        payload.setActiveSessionCount(statistics.getActiveSessions());
        payload.setHighestActiveSessionCount(statistics.getHighestActiveSessions());
        payload.setEvictedSessionCount(statistics.getEvictedSessions());
        payload.setRejectedSessionCount(statistics.getRejectedSessions());
        payload.setElapsedTime(formatDuration(statistics.getStartTime()));
        return payload;
    }

    @NonNull
    private JsonString[] getAllActiveSessions() {
        Set<String> sessionIds = sessionHandler.getActiveSessions();
        List<JsonString> list = new ArrayList<>(sessionIds.size());
        for (String sessionId : sessionIds) {
            DefaultSession session = sessionHandler.getSession(sessionId);
            if (session != null) {
                list.add(serialize(session));
            }
        }
        return list.toArray(new JsonString[0]);
    }

    public JsonString serialize(Session session) {
        Assert.notNull(session, "Session must not be null");
        UserSession userSession = session.getAttribute(USER_SESSION_KEY);
        String username;
        if (userSession != null && userSession.getAccount() != null) {
            username = userSession.getAccount().getUsername();
        } else {
            username = null;
        }
        return new JsonBuilder()
                .nullWritable(false)
                .prettyPrint(false)
                .object()
                    .put("sessionId", session.getId())
                    .put("username", username)
                    .put("country", session.getAttribute("user.countryCode"))
                    .put("createAt", formatTime(session.getCreationTime()))
                .endObject()
                .toJsonString();
    }

    @NonNull
    private String formatTime(long time) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        return date.toString();
    }

    @NonNull
    private String formatDuration(long startTime) {
        Instant start = Instant.ofEpochMilli(startTime);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        long seconds = duration.getSeconds();
        return String.format(
                "%02d:%02d:%02d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60);
    }

}
