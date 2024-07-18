package app.root.appmon.endpoint.polling;

import app.root.appmon.AppMonSession;
import com.aspectran.utils.thread.AutoLock;
import com.aspectran.utils.timer.CyclicTimeout;

import java.util.concurrent.TimeUnit;

public class PollingAppMonSession implements AppMonSession {

    private final AutoLock autoLock = new AutoLock();

    private final PollingAppMonService manager;

    private final SessionExpiryTimer expiryTimer;

    private volatile int sessionTimeout;

    private volatile int pollingInterval;

    private int lastLineIndex = -1;

    private boolean expired;

    private String[] joinedGroups;

    public PollingAppMonSession(PollingAppMonService manager, int sessionTimeout, int pollingInterval) {
        this.manager = manager;
        this.sessionTimeout = sessionTimeout;
        this.pollingInterval = pollingInterval;
        this.expiryTimer = new SessionExpiryTimer();
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = Math.max(sessionTimeout, 500);
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = Math.max(pollingInterval, 500);
    }

    @Override
    public String[] getJoinedGroups() {
        return joinedGroups;
    }

    @Override
    public void saveJoinedGroups(String[] joinGroups) {
        this.joinedGroups = joinGroups;
    }

    @Override
    public void removeJoinedGroups() {
        this.joinedGroups = null;
    }

    public int getLastLineIndex() {
        return lastLineIndex;
    }

    protected void setLastLineIndex(int lastLineIndex) {
        this.lastLineIndex = lastLineIndex;
    }

    protected void access(boolean create) {
        try (AutoLock ignored = autoLock.lock()) {
            if (isValid()) {
                if (!create) {
                    expiryTimer.cancel();
                }
                expiryTimer.schedule(sessionTimeout);
            }
        }
    }

    protected void destroy() {
        try (AutoLock ignored = autoLock.lock()) {
            expiryTimer.destroy();
        }
    }

    @Override
    public boolean isValid() {
        return !isExpired();
    }

    protected boolean isExpired() {
        try (AutoLock ignored = autoLock.lock()) {
            return expired;
        }
    }

    protected AutoLock lock() {
        return autoLock.lock();
    }

    private void doExpiry() {
        try (AutoLock ignored = lock()) {
            if (!expired) {
                expired = true;
                manager.scavenge();
            }
        }
    }

    public class SessionExpiryTimer {

        private final CyclicTimeout timer;

        SessionExpiryTimer() {
            timer = new CyclicTimeout(manager.getScheduler()) {
                @Override
                public void onTimeoutExpired() {
                    doExpiry();
                }
            };
        }

        public void schedule(long delay) {
            if (delay >= 0) {
                timer.schedule(delay, TimeUnit.MILLISECONDS);
            }
        }

        public void cancel() {
            timer.cancel();
        }

        public void destroy() {
            timer.destroy();
        }
    }

}
