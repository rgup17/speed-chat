package com.speedchat.server.services;

import com.speedchat.server.models.dto.InvitationDTO;
import com.speedchat.server.models.entities.Invitation;
import com.speedchat.server.models.entities.User;
import com.speedchat.server.models.entities.UserChatroom;
import com.speedchat.server.repositiories.InvitationRepository;
import com.speedchat.server.repositiories.UserChatroomRepository;
import com.speedchat.server.repositiories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InvitationService {

    Logger LOGGER = LogManager.getLogger(InvitationService.class);

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final UserChatroomRepository userChatroomRepository;

    public InvitationService(InvitationRepository invitationRepository, UserRepository userRepository, UserChatroomRepository userChatroomRepository) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.userChatroomRepository = userChatroomRepository;
    }

    public Object getSentInvitationsByInvitationStatus(Long userId, Character inviteStatus) {
        Map<String, Object> data = new HashMap<>();
        List<InvitationDTO> invitationsByStatus = invitationRepository.findSentInvitationsByStatus(userId, inviteStatus);
        data.put("sent-invitations", invitationsByStatus);
        LOGGER.info("Fetched sent invitations with invite status {} for user {}", inviteStatus, userId);
        return data;
    }

    public Object getReceivedInvitationsByInvitationStatus(Long userId, Character inviteStatus) {
        Map<String, Object> data = new HashMap<>();
        List<InvitationDTO> invitationsByStatus = invitationRepository.findReceivedInvitationsByStatus(userId, inviteStatus);
        data.put("received-invitations", invitationsByStatus);
        LOGGER.info("Fetched received invitations with invite status {} for user {}", inviteStatus, userId);
        return data;
    }

    public void inviteUserByEmail(Long senderUserId, String recipientEmail, Long roomId) throws Exception {
        User recipientUser = userRepository.findUserByEmail(recipientEmail);
        if (recipientUser == null) {
            throw new Exception("Invalid recipient email - email does not exist");
        }
        // check if the recipient user is already in the chat room
        UserChatroom userChatroom = userChatroomRepository.findByUserIdAndRoomId(recipientUser.getUserId(), roomId);
        if (userChatroom == null) {
            throw new Exception("Recipient user is already in chatroom");
        }
        invitationRepository.save(new Invitation(senderUserId, recipientUser.getUserId(), roomId));
        LOGGER.info("Sender {} sent an invitation to recipient email {} to join room {}", senderUserId, recipientEmail, roomId);
    }

    public void updateInvitationStatus(Long userId, Long inviteId, Character inviteStatus) throws Exception {
        Optional<Invitation> invitation = invitationRepository.findById(inviteId);
        int numberOfRowsAffected = invitationRepository.updateInviteStatusByInviteId(inviteStatus, inviteId, userId);
        if (!inviteStatus.equals(Invitation.PENDING) && invitation.isPresent()) {
            if(invitation.get().getInviteStatus().equals(Invitation.PENDING)) {
                userChatroomRepository.save(new UserChatroom(userId, invitation.get().getRoomId()));
                LOGGER.info("recipient user was invited to room {}", invitation.get().getRoomId());
            }
            else {
                LOGGER.info("invite {} has already been handled", inviteId);
            }
        }
        if (numberOfRowsAffected == 0) throw new Exception("invalid invitation");
    }

}
