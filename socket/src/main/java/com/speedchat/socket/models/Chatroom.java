package com.speedchat.socket.models;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Chatroom {
    private Long roomId;
    /**
     * -- GETTER --
     *  Returns all active WebSocket sessions for broadcasting.
     */ // Added for clarity when broadcasting messages
    @Getter
    private final Map<String, WebSocketSession> sessions;

    public Chatroom(Long roomId) {
        this.roomId = roomId;
        this.sessions = new ConcurrentHashMap<>();
    }

    /**
     * Adds a new WebSocket session to the chatroom.
     */
    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    /**
     * Gets a session by sessionId if needed for direct message scenarios.
     */
    public WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Removes a session from the chatroom by sessionId.
     */
    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
