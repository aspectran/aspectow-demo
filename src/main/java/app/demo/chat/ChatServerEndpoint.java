/*
 * Copyright (c) 2018-2025 The Aspectran Project
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
package app.demo.chat;

import app.demo.chat.codec.ChatMessageDecoder;
import app.demo.chat.codec.ChatMessageEncoder;
import app.demo.chat.model.ChatMessage;
import app.demo.chat.model.payload.BroadcastAvailableUsersPayload;
import app.demo.chat.model.payload.BroadcastConnectedUserPayload;
import app.demo.chat.model.payload.BroadcastDisconnectedUserPayload;
import app.demo.chat.model.payload.BroadcastTextMessagePayload;
import app.demo.chat.model.payload.DuplicatedUserPayload;
import app.demo.chat.model.payload.SendTextMessagePayload;
import app.demo.chat.model.payload.WelcomeUserPayload;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.annotation.jsr305.Nullable;
import com.aspectran.web.websocket.jsr356.AspectranConfigurator;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint for the chat server.
 *
 * <p>Created: 29/09/2019</p>
 */
@Component
@ServerEndpoint(
        value = "/chat",
        encoders = ChatMessageEncoder.class,
        decoders = ChatMessageDecoder.class,
        configurator = AspectranConfigurator.class
)
public class ChatServerEndpoint extends InstantActivitySupport {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
    }

    @OnMessage
    public void onMessage(Session session, @NonNull ChatMessage chatMessage) {
        SendTextMessagePayload payload = chatMessage.getSendTextMessagePayload();
        if (payload != null) {
            String username = getUsername(session);
            switch (payload.getType()) {
                case CHAT:
                    if (username != null) {
                        broadcastTextMessage(session, username, payload.getContent());
                    }
                    break;
                case JOIN:
                    if (username == null) {
                        username = payload.getUsername();
                        if (sessions.containsKey(username)) {
                            duplicatedUser(session, username);
                            return;
                        }
                        setUsername(session, username);
                        sessions.put(username, session);
                        welcomeUser(session, username);
                        broadcastUserConnected(session, username);
                        broadcastAvailableUsers();
                    }
                    break;
                case LEAVE:
                    if (username != null) {
                        leaveUser(username);
                    }
                    break;
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String username = getUsername(session);
        if (username != null) {
            leaveUser(username);
        }
    }

    @Nullable
    private String getUsername(@NonNull Session session) {
        if (session.getUserProperties().get("username") != null) {
            return session.getUserProperties().get("username").toString();
        } else {
            return null;
        }
    }

    private void setUsername(@NonNull Session session, String username) {
        session.getUserProperties().put("username", username);
    }

    private void welcomeUser(@NonNull Session session, String username) {
        WelcomeUserPayload payload = new WelcomeUserPayload();
        payload.setUsername(username);
        session.getAsyncRemote().sendObject(new ChatMessage(payload));
    }

    private void duplicatedUser(@NonNull Session session, String username) {
        DuplicatedUserPayload payload = new DuplicatedUserPayload();
        payload.setUsername(username);
        session.getAsyncRemote().sendObject(new ChatMessage(payload));
    }

    private void leaveUser(String username) {
        sessions.remove(username);
        broadcastUserDisconnected(username);
        broadcastAvailableUsers();
    }

    private void broadcastUserConnected(Session session, String username) {
        BroadcastConnectedUserPayload payload = new BroadcastConnectedUserPayload();
        payload.setUsername(username);
        broadcast(new ChatMessage(payload), session);
    }

    private void broadcastUserDisconnected(String username) {
        BroadcastDisconnectedUserPayload payload = new BroadcastDisconnectedUserPayload();
        payload.setUsername(username);
        broadcast(new ChatMessage(payload));
    }

    private void broadcastTextMessage(Session session, String username, String text) {
        BroadcastTextMessagePayload payload = new BroadcastTextMessagePayload();
        payload.setContent(text);
        payload.setUsername(username);
        broadcast(new ChatMessage(payload), session);
    }

    private void broadcastAvailableUsers() {
        BroadcastAvailableUsersPayload payload = new BroadcastAvailableUsersPayload();
        payload.setUsernames(sessions.keySet());
        broadcast(new ChatMessage(payload));
    }

    private void broadcast(ChatMessage message) {
        synchronized (sessions) {
            for (Session session : sessions.values()) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendObject(message);
                }
            }
        }
    }

    private void broadcast(ChatMessage message, Session ignoredSession) {
        synchronized (sessions) {
            for (Session session : sessions.values()) {
                if (session.isOpen() && !session.getId().equals(ignoredSession.getId())) {
                    session.getAsyncRemote().sendObject(message);
                }
            }
        }
    }

}
