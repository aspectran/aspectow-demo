package app.root.appmon.endpoint.polling;

import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.annotation.jsr305.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PollingAppMonBuffer {

    private final AtomicInteger lineCounter = new AtomicInteger(0);

    private final List<String> buffer = new ArrayList<>();

    public int push(String line) {
        synchronized (buffer) {
            buffer.add(line);
            return lineCounter.incrementAndGet();
        }
    }

    @Nullable
    public String[] pop(@NonNull PollingAppMonSession session) {
        synchronized (buffer) {
            int lineIndex = session.getLastLineIndex();
            if (lineIndex < 0) {
                if (buffer.isEmpty()) {
                    return null;
                } else {
                    session.setLastLineIndex(buffer.size() - 1);
                    return buffer.toArray(new String[0]);
                }
            }
            int maxLineIndex = lineCounter.get() - 1;
            if (lineIndex < maxLineIndex) {
                session.setLastLineIndex(maxLineIndex);
                int offset = maxLineIndex - lineIndex;
                if (offset < buffer.size()) {
                    int start = buffer.size() - offset;
                    return buffer.subList(start, buffer.size()).toArray(new String[0]);
                } else {
                    return buffer.toArray(new String[0]);
                }
            } else if (lineIndex > maxLineIndex) {
                session.setLastLineIndex(maxLineIndex);
                return null;
            } else {
                return null;
            }
        }
    }

    public void remove(int minLineIndex) {
        synchronized (buffer) {
            int toIndex = lineCounter.get() - minLineIndex;
            if (toIndex < buffer.size()) {
                if (toIndex > 0) {
                    buffer.subList(0, toIndex + 1).clear();
                } else if (toIndex == 0) {
                    buffer.remove(toIndex);
                }
            }
        }
    }

}
