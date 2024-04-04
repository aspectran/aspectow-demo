package app.root.appmon.measurement.session;

import app.jpetstore.common.user.UserSession;
import com.aspectran.core.component.session.DefaultSession;
import com.aspectran.core.component.session.SessionHandler;
import com.aspectran.core.component.session.SessionStatistics;
import com.aspectran.undertow.server.TowServer;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static app.jpetstore.common.user.UserSessionManager.USER_SESSION_KEY;

public class TowSessionStats {

    private final TowServer towServer;

    private final String deploymentName;

    private SessionStatsPayload oldStats;

    public TowSessionStats(TowServer towServer, String deploymentName) {
        this.towServer = towServer;
        this.deploymentName = deploymentName;
    }

    public void join() {
        oldStats = null;
    }

    public SessionStatsPayload getSessionStatsPayload() {
        SessionHandler sessionHandler = towServer.getSessionHandler(deploymentName);
        SessionStatistics statistics = sessionHandler.getStatistics();

        SessionStatsPayload stats = new SessionStatsPayload();
        stats.setCreatedSessionCount(statistics.getCreatedSessions());
        stats.setExpiredSessionCount(statistics.getExpiredSessions());
        stats.setActiveSessionCount(statistics.getActiveSessions());
        stats.setHighestActiveSessionCount(statistics.getHighestActiveSessions());
        stats.setEvictedSessionCount(statistics.getEvictedSessions());
        stats.setRejectedSessionCount(statistics.getRejectedSessions());
        stats.setElapsedTime(formatDuration(statistics.getStartTime()));

        // Current sessions
        List<String> currentSessions = new ArrayList<>();
        Set<String> sessionIds = sessionHandler.getActiveSessions();
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
        stats.setCurrentSessions(currentSessions.toArray(new String[0]));

        if (!stats.equals(oldStats)) {
            oldStats = stats;
            return stats;
        } else {
            return null;
        }
    }

    @NonNull
    private String formatTime(long time) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        return date.toString();
    }

    private static String formatDuration(long startTime) {
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
