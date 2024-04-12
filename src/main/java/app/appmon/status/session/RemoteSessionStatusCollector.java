package app.appmon.status.session;

import app.appmon.status.StatusCollector;
import app.appmon.status.StatusInfo;
import app.appmon.status.StatusManager;
import app.jpetstore.user.UserSession;
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

public class RemoteSessionStatusCollector implements StatusCollector {
    @Override
    public void init() {

    }

    @Override
    public String collect() {
        return "";
    }

//    private static final Logger logger = LoggerFactory.getLogger(RemoteSessionStatusCollector.class);
//
//    private static final StatusManager manager;
//
//    private static final String source;
//
//    public RemoteSessionStatusCollector(@NonNull StatusManager manager,
//                                        @NonNull StatusInfo info) {
//        this.manager = manager;
//        this.source = info.getSource();
//    }
//
//    @Override
//    public void init() {
//    }
//
//    @Override
//    public String collect() {
//        try {
//        } catch (Exception e) {
//            logger.error(e);
//            return null;
//        }
//    }
//
//    @NonNull
//    private String[] getCurrentSessions() {
//    }
//
//    @NonNull
//    private String formatTime(long time) {
//        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
//        return date.toString();
//    }
//
//    private String formatDuration(long startTime) {
//        Instant start = Instant.ofEpochMilli(startTime);
//        Instant end = Instant.now();
//        Duration duration = Duration.between(start, end);
//        long seconds = duration.getSeconds();
//        return String.format(
//                "%02d:%02d:%02d",
//                seconds / 3600,
//                (seconds % 3600) / 60,
//                seconds % 60);
//    }

}
