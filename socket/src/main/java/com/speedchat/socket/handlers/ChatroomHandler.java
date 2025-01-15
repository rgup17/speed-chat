package com.speedchat.socket.handlers;

import com.google.gson.Gson;
import com.speedchat.socket.models.ChatMessage;
import com.speedchat.socket.redis.RedisMessagePublisher;
import com.speedchat.socket.services.ChatroomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;

@Component
public class ChatroomHandler extends TextWebSocketHandler {

    @Value("${kafka-props.chat-messages-topic}")
    private String chatMessageTopic;
    @Value("${server-props.server-name}")
    private String serverName;

    private final Logger logger = LogManager.getLogger(ChatroomHandler.class);
    private final Gson gson;
    private final ChatroomService chatroomService;
    private final RedisMessagePublisher redisPub;
    private final KafkaTemplate<String, String> kafkaProducer;

    public ChatroomHandler(Gson gson, ChatroomService chatroomService, RedisMessagePublisher redisPub,
                           KafkaTemplate<String, String> kafkaProducer) {
        this.gson = gson;
        this.chatroomService = chatroomService;
        this.redisPub = redisPub;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long roomId = (Long) session.getAttributes().get("roomId");
        JSONObject userData = (JSONObject) session.getAttributes().get("userData");
        logger.info("{} connected to room ID: {} on server {}", userData.getString("userName"), roomId, serverName);

        chatroomService.addSessionToChatroom(roomId, session);

        // Broadcasting a join message using Redisson
        publishMessage(roomId, new ChatMessage(
                "N",
                String.format("%s joined the chat!", userData.getString("userName")),
                userData.getLong("userId"),
                userData.getString("userName"),
                roomId,
                Instant.now().toEpochMilli()
        ));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long roomId = (Long) session.getAttributes().get("roomId");
        JSONObject userData = (JSONObject) session.getAttributes().get("userData");
        logger.info("{} disconnected from room ID: {} on server {}", userData.getString("userName"), roomId, serverName);

        chatroomService.removeSessionFromChatroom(roomId, session.getId());

        // Broadcasting a leave message using Redisson
        publishMessage(roomId, new ChatMessage(
                "N",
                String.format("%s left the chat!", userData.getString("userName")),
                userData.getLong("userId"),
                userData.getString("userName"),
                roomId,
                Instant.now().toEpochMilli()
        ));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JSONObject userData = (JSONObject) session.getAttributes().get("userData");
            Long roomId = (Long) session.getAttributes().get("roomId");

            ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
            chatMessage.setUserId(userData.getLong("userId"));
            chatMessage.setUsername(userData.getString("userName"));
            chatMessage.setRoomId(roomId);
            chatMessage.setTimestamp(Instant.now().toEpochMilli());

            // Publish message using Redis Pub/Sub and Kafka
            publishMessage(roomId, chatMessage);
            logger.info("Text message received from {} in room {}.", userData.getString("userName"), roomId);
        } catch (Exception e) {
            logger.error("Error handling text message: {}", e.getMessage());
        }
    }

    @Async("cachedThreadPool")
    public void publishMessage(Long roomId, ChatMessage chatMessage) {
        // Redis pub/sub for real-time messaging
        redisPub.publish(roomId, chatMessage);

        // Kafka for persistent logging and analytics
        if (chatMessage.getType() == null) {
            kafkaProducer.send(chatMessageTopic, gson.toJson(chatMessage));
        }
    }
}
