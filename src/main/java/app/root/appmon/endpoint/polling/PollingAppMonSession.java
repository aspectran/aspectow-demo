package app.root.appmon.endpoint.polling;

import app.root.appmon.AppMonSession;
import com.aspectran.utils.thread.AutoLock;
import com.aspectran.utils.timer.CyclicTimeout;

import java.util.concurrent.TimeUnit;

public class PollingAppMonSession implements AppMonSession {

    private final AutoLock autoLock = new AutoLock();

    private final PollingAppMonSessionManager manager;

    private final SessionExpiryTimer expiryTimer;

    private volatile long pollingInterval;

    private int lastLineIndex = -1;

    private long lastAccessed;

    private boolean expired;

    private String[] joinedGroups;

    public PollingAppMonSession(PollingAppMonSessionManager manager, long pollingInterval) {
        this.manager = manager;
        this.pollingInterval = pollingInterval;
        this.expiryTimer = new SessionExpiryTimer();
    }

    public long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(long pollingInterval) {
        try (AutoLock ignored = autoLock.lock()) {
            this.pollingInterval = Math.max(pollingInterval, 500L);
        }
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
                lastAccessed = System.currentTimeMillis();
                if (!create) {
                    expiryTimer.cancel();
                }
                expiryTimer.schedule(pollingInterval);
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

    private boolean checkExpired() {
        long now = System.currentTimeMillis();
        expired = (lastAccessed + pollingInterval <= now);
        if (expired) {
            manager.scavenge();
        }
        return expired;
    }

    public class SessionExpiryTimer {

        private final CyclicTimeout timer;

        SessionExpiryTimer() {
            timer = new CyclicTimeout(manager.getScheduler()) {
                @Override
                public void onTimeoutExpired() {
                    try (AutoLock ignored = lock()) {
                        if (!checkExpired()) {
                            SessionExpiryTimer.this.schedule(pollingInterval);
                        }
                    }
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
