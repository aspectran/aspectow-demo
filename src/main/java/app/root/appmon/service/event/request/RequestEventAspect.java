package app.root.appmon.service.event.request;

import app.root.appmon.service.event.EventService;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.json.JsonBuilder;

/**
 * <p>Created: 2024-12-19</p>
 */
public class RequestEventAspect {

    private final EventService eventService;

    public RequestEventAspect(EventService eventService) {
        this.eventService = eventService;
    }

    public void request(@NonNull Activity activity) {
        JsonBuilder jsonBuilder = new JsonBuilder();
        SessionAdapter sessionAdapter = activity.getSessionAdapter();
        if (sessionAdapter != null) {
            jsonBuilder.put("sessionid", sessionAdapter.getId());
        }

        eventService.broadcast("request:start:" + jsonBuilder);
    }

    public void complete(@NonNull Activity activity) {
        JsonBuilder jsonBuilder = new JsonBuilder();
        SessionAdapter sessionAdapter = activity.getSessionAdapter();
        if (sessionAdapter != null) {
            jsonBuilder.put("sessionid", sessionAdapter.getId());
        }

        eventService.broadcast("request:end:" + jsonBuilder);
    }

}
