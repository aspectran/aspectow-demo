package app.root.appmon.status.session;

import app.jpetstore.user.UserSession;
import app.root.appmon.status.StatusInfo;
import app.root.appmon.status.StatusManager;
import app.root.appmon.status.StatusReader;
import com.aspectran.core.component.session.DefaultSession;
import com.aspectran.core.component.session.SessionHandler;
import com.aspectran.core.component.session.SessionStatistics;
import com.aspectran.undertow.server.TowServer;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static app.jpetstore.user.UserSessionManager.USER_SESSION_KEY;

public class LocalSessionStatusReader implements StatusReader {

    private static final Logger logger = LoggerFactory.getLogger(LocalSessionStatusReader.class);

    private final SessionHandler sessionHandler;

    private SessionStatusPayload oldPayload;

    public LocalSessionStatusReader(@NonNull StatusManager manager,
                                    @NonNull StatusInfo info) {
        TowServer towServer = manager.getBean(info.getSource());
        this.sessionHandler = towServer.getSessionHandler(info.getName());
    }

    @Override
    public void init() {
        oldPayload = null;
    }

    @Override
    public String read() {
        try {
            SessionStatusPayload payload = load();
            return payload.toJson();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public String readIfChanged() {
        try {
            SessionStatusPayload payload = load();
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

    @NonNull
    private SessionStatusPayload load() {
        SessionStatistics statistics = sessionHandler.getStatistics();

        SessionStatusPayload payload = new SessionStatusPayload();
        payload.setCreatedSessionCount(statistics.getCreatedSessions());
        payload.setExpiredSessionCount(statistics.getExpiredSessions());
        payload.setActiveSessionCount(statistics.getActiveSessions());
        payload.setHighestActiveSessionCount(statistics.getHighestActiveSessions());
        payload.setEvictedSessionCount(statistics.getEvictedSessions());
        payload.setRejectedSessionCount(statistics.getRejectedSessions());
        payload.setElapsedTime(formatDuration(statistics.getStartTime()));
        payload.setCurrentSessions(getCurrentSessions());
        return payload;
    }

    @NonNull
    private String[] getCurrentSessions() {
        Set<String> sessionIds = sessionHandler.getActiveSessions();
        List<String> currentSessions = new ArrayList<>(sessionIds.size());
        for (String sessionId : sessionIds) {
            DefaultSession session = sessionHandler.getSession(sessionId);
            if (session != null) {
                UserSession userSession = session.getAttribute(USER_SESSION_KEY);
                String loggedIn = (userSession != null && userSession.isAuthenticated() ? "1" : "0");
                String username = (userSession != null && userSession.getAccount() != null ?
                        "(" + userSession.getAccount().getUsername() + ") " : "");
                currentSessions.add(loggedIn + ":" + username + "Session " + session.getId() + " created at " +
                        formatTime(session.getCreationTime()));
            }
        }
        return currentSessions.toArray(new String[0]);
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
