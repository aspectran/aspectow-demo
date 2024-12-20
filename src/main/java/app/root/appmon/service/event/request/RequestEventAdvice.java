package app.root.appmon.service.event.request;

import app.root.appmon.service.event.EventService;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.json.JsonBuilder;

/**
 * <p>Created: 2024-12-19</p>
 */
public class RequestEventAdvice {

    private final EventService eventService;

    private long startTime;

    public RequestEventAdvice(EventService eventService) {
        this.eventService = eventService;
    }

    public void request(@NonNull Activity activity) {
        startTime = System.currentTimeMillis();
//
//        JsonBuilder jsonBuilder = new JsonBuilder()
//                .prettyPrint(false)
//                .nullWritable(false)
//                .object()
//                    .put("startTime", startTime)
//                    .put("thread", Thread.currentThread().getName());
//
//        SessionAdapter sessionAdapter = activity.getSessionAdapter();
//        if (sessionAdapter != null) {
//            jsonBuilder.put("sessionId", sessionAdapter.getId());
//        }
//
//        jsonBuilder.endObject();
//        eventService.broadcast("start:" + jsonBuilder);
    }

    public void complete(@NonNull Activity activity) {
        long elapsedTime = System.currentTimeMillis() - startTime;

        JsonBuilder jsonBuilder = new JsonBuilder()
                .prettyPrint(false)
                .nullWritable(false)
                .object()
                    .put("startTime", startTime)
                    .put("elapsedTime", elapsedTime)
                    .put("thread", Thread.currentThread().getName());

        SessionAdapter sessionAdapter = activity.getSessionAdapter();
        if (sessionAdapter != null) {
            jsonBuilder.put("sessionId", sessionAdapter.getId());
        }

        Throwable error = activity.getRootCauseOfRaisedException();
        if (error != null) {
            jsonBuilder.put("error", error);
        }

        jsonBuilder.endObject();
        eventService.broadcast(jsonBuilder.toString());
    }

}
