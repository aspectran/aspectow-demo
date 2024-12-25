package app.root.appmon.exporter.state.session;

import com.aspectran.core.component.session.Session;
import com.aspectran.core.component.session.SessionListener;
import com.aspectran.utils.annotation.jsr305.NonNull;

import static app.root.appmon.exporter.state.session.SessionStateReader.USER_SESSION_KEY;

/**
 * <p>Created: 2024-12-13</p>
 */
public class SessionStateListener implements SessionListener {

    private final SessionStateReader statusReader;

    public SessionStateListener(SessionStateReader statusReader) {
        this.statusReader = statusReader;
    }

    @Override
    public void sessionCreated(@NonNull Session session) {
        String json = statusReader.readWithCreatedSession(session);
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void sessionDestroyed(@NonNull Session session) {
        String json = statusReader.readWithDestroyedSession(session.getId());
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void sessionEvicted(@NonNull Session session) {
        String json = statusReader.readWithEvictedSession(session.getId());
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void sessionResided(@NonNull Session session) {
        String json = statusReader.readWithResidedSession(session);
        statusReader.getStateExporter().broadcast(json);
    }

    @Override
    public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
        if (USER_SESSION_KEY.equals(name)) {
            String json = statusReader.readWithCreatedSession(session);
            statusReader.getStateExporter().broadcast(json);
        }
    }

}
