package com.speedchat.socket.services;

import com.google.gson.Gson;
import com.speedchat.socket.models.ChatMessage;
import com.speedchat.socket.models.Chatroom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatroomService {

    @Value("${server-props.server-name}")
    private String serverName;

    private final Map<Long, Chatroom> chatrooms = new ConcurrentHashMap<>();
    private final Logger logger = LogManager.getLogger(ChatroomService.class);
    private final Gson gson;

    public ChatroomService(Gson gson) {
        this.gson = gson;
    }

    /**
     * Adds a WebSocket session to the specified chatroom.
     */
    public void addSessionToChatroom(Long roomId, WebSocketSession session) {
        chatrooms.computeIfAbsent(roomId, Chatroom::new).addSession(session);
        logger.info("Session with ID: {} added to room with ID: {}", session.getId(), roomId);
    }

    /**
     * Removes a WebSocket session from the specified chatroom.
     */
    public void removeSessionFromChatroom(Long roomId, String sessionId) {
        Chatroom room = chatrooms.get(roomId);
        if (room != null) {
            room.deleteSession(sessionId);
            logger.info("Session with ID: {} removed from room with ID: {}", sessionId, roomId);

            // Remove room if no sessions are left
            if (room.getSessions().isEmpty()) {
                chatrooms.remove(roomId);
                logger.info("Chatroom with ID: {} has been removed due to no active sessions.", roomId);
            }
        } else {
            logger.warn("Attempted to remove session from non-existent room with ID: {}", roomId);
        }
    }

    /**
     * Broadcasts a message to all users in a chatroom asynchronously.
     */
    @Async("cachedThreadPool")
    public void sendMessageToChatroom(ChatMessage message, Long roomId) throws Exception {
        Chatroom room = chatrooms.get(roomId);
        if (room == null) {
            throw new Exception("Chatroom with ID: " + roomId + " not found on server: " + serverName);
        }

        for (Map.Entry<String, WebSocketSession> entry : room.getSessions().entrySet()) {
            try {
                WebSocketSession session = entry.getValue();
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(gson.toJson(message)));
                } else {
                    logger.warn("Session with ID: {} is closed and cannot receive messages.", entry.getKey());
                }
            } catch (IOException e) {
                logger.error("Error sending message to session ID: {}", entry.getKey(), e);
            }
        }
    }

    /**
     * Returns all active chatrooms for administrative purposes.
     */
    public Map<Long, Chatroom> getChatrooms() {
        return this.chatrooms;
    }
}
