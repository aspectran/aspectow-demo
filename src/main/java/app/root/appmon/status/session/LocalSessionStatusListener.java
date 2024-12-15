package app.root.appmon.status.session;

import com.aspectran.core.component.session.Session;
import com.aspectran.core.component.session.SessionListener;
import com.aspectran.utils.annotation.jsr305.NonNull;

import static app.root.appmon.status.session.LocalSessionStatusReader.USER_SESSION_KEY;

/**
 * <p>Created: 2024-12-13</p>
 */
public class LocalSessionStatusListener implements SessionListener {

    private final LocalSessionStatusReader statusReader;

    public LocalSessionStatusListener(LocalSessionStatusReader statusReader) {
        this.statusReader = statusReader;
    }

    @Override
    public void sessionCreated(@NonNull Session session) {
        String json = statusReader.readWithCreatedSession(session);
        statusReader.getStatusService().broadcast(json);
    }

    @Override
    public void sessionDestroyed(@NonNull Session session) {
        String json = statusReader.readWithDestroyedSession(session.getId());
        statusReader.getStatusService().broadcast(json);
    }

    @Override
    public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
        if (USER_SESSION_KEY.equals(name)) {
            String json = statusReader.readWithCreatedSession(session);
            statusReader.getStatusService().broadcast(json);
        }
    }

}
