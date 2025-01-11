package com.speedchat.server.services;

import com.speedchat.server.models.dto.MessageDTO;
import com.speedchat.server.models.dto.ParticipantDTO;
import com.speedchat.server.models.entities.Chatroom;
import com.speedchat.server.models.entities.UserChatroom;
import com.speedchat.server.repositiories.ChatroomRepository;
import com.speedchat.server.repositiories.MessageRepository;
import com.speedchat.server.repositiories.UserChatroomRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatroomService {
    private final Logger LOGGER = LogManager.getLogger(ChatroomService.class);
    private final ChatroomRepository chatroomRepository;
    private final UserChatroomRepository userChatroomRepository;
    private final MessageRepository messageRepository;

    public ChatroomService(ChatroomRepository chatroomRepository, UserChatroomRepository userChatroomRepository, MessageRepository messageRepository) {
        this.chatroomRepository = chatroomRepository;
        this.userChatroomRepository = userChatroomRepository;
        this.messageRepository = messageRepository;
    }

    public Object createChatroom(Long userId, String chatroomName) {
        Chatroom chatroom = chatroomRepository.save(new Chatroom(chatroomName, userId));
        userChatroomRepository.save(new UserChatroom(userId, chatroom.getRoomId()));
        LOGGER.info("Successfully created chatroom {} for {}", chatroomName, userId);
        Map<String, Object> data = new HashMap<>();
        data.put("chatroom", chatroom);
        return data;
    }

    public void updateChatRoomName(Long userId, Long roomId, String newChatroomName) throws Exception {
        int numRowsAffected = chatroomRepository.updateRoomNameByRoomId(newChatroomName, roomId, userId);
        LOGGER.info("Updated chatroom name to {} for user {}", newChatroomName, userId);
        if (numRowsAffected == 0) throw new Exception("Cannot update chatroom");
    }

    public void leaveChatroom(Long userId, Long roomId) throws Exception{
        int numRowsAffected = userChatroomRepository.exitRoomByUserIdAndRoomId(userId, roomId);
        LOGGER.info("Left chatroom with roomId {} for userId {}", roomId, userId);
        if (numRowsAffected == 0) throw new Exception("User could not leave chatroom");
    }

    public void deactivateChatroom(Long userId, Long roomId) {
        int numRowsAffected = chatroomRepository.deactivateRoomByRoomIdAndUserId('D', roomId, userId);
        LOGGER.info("Deactivated room {} for user {}", roomId, userId);
    }

    public Object findParticipantsInChatroom(Long roomId) {
        List<ParticipantDTO> participants = userChatroomRepository.findParticipantsByRoomId(roomId);
        Map<String, Object> data = new HashMap<>();
        data.put("participants", participants);
        LOGGER.info("Successfully fetched participants in {}", roomId);
        return data;
    }

    public Object getMessagesInChatroom(Long roomId, Integer pageNumber, Integer pageLimit) {
        List<MessageDTO> messagesInChatRoom = messageRepository.findMessagesByRoomId(roomId, pageLimit, pageNumber);
        LOGGER.info("messages in chatroom {} : {}", roomId, messagesInChatRoom);
        Map<String, Object> data = new HashMap<>();
        data.put("messages", messagesInChatRoom);
        LOGGER.info("Successfully fetched messsges in chatroom {}", roomId);
        return data;
    }






}
