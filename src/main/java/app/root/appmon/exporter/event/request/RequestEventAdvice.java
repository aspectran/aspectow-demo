package app.root.appmon.exporter.event.request;

import app.root.appmon.exporter.event.EventExporter;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.json.JsonBuilder;

/**
 * <p>Created: 2024-12-19</p>
 */
public class RequestEventAdvice {

    private final EventExporter eventExporter;

    private long startTime;

    private String sessionId;

    public RequestEventAdvice(EventExporter eventExporter) {
        this.eventExporter = eventExporter;
    }

    public void request(@NonNull Activity activity) {
        startTime = System.currentTimeMillis();

        // Since the servlet container does not allow session creation after
        // the response is committed, the session ID must be secured in advance.
        SessionAdapter sessionAdapter = activity.getSessionAdapter();
        if (sessionAdapter != null) {
            sessionId = sessionAdapter.getId();
        }
    }

    public void complete(@NonNull Activity activity) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        Throwable error = activity.getRootCauseOfRaisedException();

        String json = new JsonBuilder()
                .prettyPrint(false)
                .nullWritable(false)
                .object()
                    .put("startTime", startTime)
                    .put("elapsedTime", elapsedTime)
                    .put("thread", Thread.currentThread().getName())
                    .put("sessionId", sessionId)
                    .put("error", error)
                .endObject()
                .toString();
        eventExporter.broadcast(json);
    }

}
