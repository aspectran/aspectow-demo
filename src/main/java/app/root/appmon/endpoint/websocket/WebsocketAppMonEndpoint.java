/*
 * Copyright (c) ${project.inceptionYear}-2024 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.root.appmon.endpoint.websocket;

import app.root.appmon.AppMonEndpoint;
import app.root.appmon.AppMonManager;
import app.root.appmon.group.GroupInfo;
import app.root.appmon.group.GroupManager;
import app.root.appmon.logtail.LogtailInfo;
import app.root.appmon.status.StatusInfo;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Initialize;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.json.JsonWriter;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;
import com.aspectran.utils.security.InvalidPBTokenException;
import com.aspectran.utils.security.TimeLimitedPBTokenIssuer;
import com.aspectran.web.websocket.jsr356.AspectranConfigurator;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@ServerEndpoint(
        value = "/appmon/server/endpoint/{token}",
        configurator = AspectranConfigurator.class
)
@AvoidAdvice
public class WebsocketAppMonEndpoint implements AppMonEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketAppMonEndpoint.class);

    private static final String HEARTBEAT_PING_MSG = "--ping--";

    private static final String HEARTBEAT_PONG_MSG = "--pong--";

    private static final String MESSAGE_JOIN = "join:";

    private static final String MESSAGE_LEAVE = "leave";

    private static final String MESSAGE_JOINED = "joined:";

    private static final String MESSAGE_ESTABLISHED = "established:";

    private static final Set<WebsocketAppMonSession> sessions = Collections.synchronizedSet(new HashSet<>());

    private final AppMonManager appMonManager;

    @Autowired
    public WebsocketAppMonEndpoint(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
    }

    @Initialize
    public void registerEndpoint() {
        appMonManager.putEndpoint(this);
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) throws IOException {
        try {
            TimeLimitedPBTokenIssuer.validate(token);
        } catch (InvalidPBTokenException e) {
            logger.error("Invalid token: " + token);
            String reason = "Invalid token";
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, reason));
            throw new IOException(reason, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("WebSocket connection established with token: " + token);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        if (HEARTBEAT_PING_MSG.equals(message)) {
            session.getAsyncRemote().sendText(HEARTBEAT_PONG_MSG);
            return;
        }
        if (message != null && message.startsWith(MESSAGE_JOIN)) {
            addSession(session, message.substring(MESSAGE_JOIN.length()));
        } else if (MESSAGE_ESTABLISHED.equals(message)) {
            establishComplete(session);
        } else if (MESSAGE_LEAVE.equals(message)) {
            removeSession(session);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        if (logger.isDebugEnabled()) {
            logger.debug("Websocket session " + session.getId() + " has been closed. Reason: " + reason);
        }
        removeSession(session);
    }

    @OnError
    public void onError(@NonNull Session session, Throwable error) {
        logger.error("Error in websocket session: " + session.getId(), error);
        try {
            removeSession(session);
            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, null));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void addSession(Session session, String message) throws IOException {
        WebsocketAppMonSession appMonSession = new WebsocketAppMonSession(session);
        if (sessions.add(appMonSession)) {
            String[] joinGroups = StringUtils.splitCommaDelimitedString(message);
            List<GroupInfo> groupInfoList = sendJoined(session, joinGroups);
            if (!groupInfoList.isEmpty()) {
                String[] groupNames = GroupManager.extractGroupNames(groupInfoList);
                appMonSession.saveJoinedGroups(groupNames);
            }
        }
    }

    private List<GroupInfo> sendJoined(@NonNull Session session, String[] joinGroups) throws IOException {
        List<GroupInfo> groups = appMonManager.getGroupInfoList(joinGroups);
        List<LogtailInfo> logtails = appMonManager.getLogtailInfoList(joinGroups);
        List<StatusInfo> statuses = appMonManager.getStatusInfoList(joinGroups);
        JsonWriter jsonWriter = new JsonWriter().nullWritable(false);
        jsonWriter.beginObject();
        jsonWriter.writeName("groups").write(groups);
        jsonWriter.writeName("logtails").write(logtails);
        jsonWriter.writeName("statuses").write(statuses);
        jsonWriter.endObject();
        session.getAsyncRemote().sendText(MESSAGE_JOINED + jsonWriter);
        return groups;
    }

    private void establishComplete(@NonNull Session session) {
        appMonManager.join(new WebsocketAppMonSession(session));
    }

    private void removeSession(Session session) {
        WebsocketAppMonSession appMonSession = new WebsocketAppMonSession(session);
        if (sessions.remove(appMonSession)) {
            appMonManager.release(appMonSession);
        }
    }

    @Override
    public void broadcast(String message) {
        synchronized (sessions) {
            for (WebsocketAppMonSession appMonSession : sessions) {
                if (appMonSession.getSession().isOpen()) {
                    appMonSession.getSession().getAsyncRemote().sendText(message);
                }
            }
        }
    }

    @Override
    public boolean isUsingGroup(String group) {
        if (StringUtils.hasLength(group)) {
            for (WebsocketAppMonSession appMonSession : sessions) {
                String[] savedGroups = appMonSession.getJoinedGroups();
                if (savedGroups != null) {
                    for (String saved : savedGroups) {
                        if (group.equals(saved)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
