package app.root.appmon.endpoint.polling;

import app.root.appmon.AppMonManager;
import com.aspectran.core.component.AbstractComponent;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.thread.ScheduledExecutorScheduler;
import com.aspectran.utils.thread.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PollingAppMonSessionManager extends AbstractComponent {

    private final Map<String, PollingAppMonSession> sessions = new ConcurrentHashMap<>();

    private final Scheduler scheduler = new ScheduledExecutorScheduler("PollingAppMonSessionScheduler", false);

    private final AppMonManager appMonManager;

    public PollingAppMonSessionManager(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
    }

    public PollingAppMonSession createSession(String id, int pollingInterval) {
        PollingAppMonSession existingSession = sessions.get(id);
        if (existingSession != null) {
            existingSession.access(false);
            return existingSession;
        } else {
            PollingAppMonSession session = new PollingAppMonSession(this, pollingInterval);
            sessions.put(id, session);
            session.access(true);
            return session;
        }
    }

    public PollingAppMonSession getSession(String id) {
        PollingAppMonSession session = sessions.get(id);
        if (session != null) {
            session.access(false);
            return session;
        } else {
            return null;
        }
    }

    protected void scavenge() {
        List<String> expiredSessions = new ArrayList<>();
        for (Map.Entry<String, PollingAppMonSession> entry : sessions.entrySet()) {
            String id = entry.getKey();
            PollingAppMonSession session = entry.getValue();
            if (session.isExpired()) {
                appMonManager.release(session);
                session.destroy();
                expiredSessions.add(id);
            }
        }
        for (String id : expiredSessions) {
            sessions.remove(id);
        }
    }

    protected boolean isUsingGroup(String group) {
        if (StringUtils.hasLength(group)) {
            for (PollingAppMonSession session : sessions.values()) {
                if (session.isValid()) {
                    String[] savedGroups = session.getJoinedGroups();
                    if (savedGroups != null) {
                        for (String saved : savedGroups) {
                            if (group.equals(saved)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    protected int getMinLineIndex() {
        int minLineIndex = -1;
        for (PollingAppMonSession session : sessions.values()) {
            if (session.isValid()) {
                if (minLineIndex == -1) {
                    minLineIndex = session.getLastLineIndex();
                } else if (session.getLastLineIndex() < minLineIndex) {
                    minLineIndex = session.getLastLineIndex();
                }
            }
        }
        return minLineIndex;
    }

    protected Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    protected void doInitialize() throws Exception {
        scheduler.start();
    }

    @Override
    protected void doDestroy() throws Exception {
        scheduler.stop();
    }

}
