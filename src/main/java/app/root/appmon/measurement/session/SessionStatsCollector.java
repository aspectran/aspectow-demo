package app.root.appmon.measurement.session;

import app.jpetstore.user.UserSession;
import app.root.appmon.measurement.MeasureCollector;
import app.root.appmon.measurement.MeasurementInfo;
import app.root.appmon.measurement.MeasurementManager;
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

public class SessionStatsCollector implements MeasureCollector {

    private static final Logger logger = LoggerFactory.getLogger(SessionStatsCollector.class);

    private final SessionHandler sessionHandler;

    private SessionStatsPayload oldStats;

    public SessionStatsCollector(@NonNull MeasurementManager manager,
                                 @NonNull MeasurementInfo info) {
        TowServer towServer = manager.getBean(info.getSource());
        this.sessionHandler = towServer.getSessionHandler(info.getName());
    }

    @Override
    public void init() {
        oldStats = null;
    }

    @Override
    public String collect() {
        try {
            SessionStatistics statistics = sessionHandler.getStatistics();

            SessionStatsPayload stats = new SessionStatsPayload();
            stats.setCreatedSessionCount(statistics.getCreatedSessions());
            stats.setExpiredSessionCount(statistics.getExpiredSessions());
            stats.setActiveSessionCount(statistics.getActiveSessions());
            stats.setHighestActiveSessionCount(statistics.getHighestActiveSessions());
            stats.setEvictedSessionCount(statistics.getEvictedSessions());
            stats.setRejectedSessionCount(statistics.getRejectedSessions());
            stats.setElapsedTime(formatDuration(statistics.getStartTime()));
            stats.setCurrentSessions(getCurrentSessions());

            if (!stats.equals(oldStats)) {
                oldStats = stats;
                return stats.toJson();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
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
