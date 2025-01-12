package com.speedchat.server.repositiories;

import com.speedchat.server.models.dto.InvitationDTO;
import com.speedchat.server.models.entities.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query(value = """
        SELECT
            i.invite_id AS inviteId,
            i.room_id AS roomId,
            c.room_name AS roomName,
            i.sender_user_id AS senderUserId,
            s.user_name AS senderUsername,
            i.recipient_user_id AS recipientUserId,
            r.user_name AS recipientUsername,
            i.invite_status AS inviteStatus,
            i.invite_timestamp AS inviteTimestamp
        FROM invitation i
        JOIN chatroom c ON i.room_id = c.room_id
        JOIN users s ON i.sender_user_id = s.user_id
        JOIN users r ON i.recipient_user_id = r.user_id
        WHERE i.sender_user_id = :userId
        AND i.invite_status = :inviteStatus
        ORDER BY i.invite_timestamp DESC
    """, nativeQuery = true)
    List<InvitationDTO> findSentInvitationsByStatus(
            @Param("userId") Long userId,
            @Param("inviteStatus") char inviteStatus);

    @Query(value = """
        SELECT
            i.invite_id AS inviteId,
            i.room_id AS roomId,
            c.room_name AS roomName,
            i.sender_user_id AS senderUserId,
            s.user_name AS senderUserName,
            i.recipient_user_id AS recipientUserId,
            r.user_name AS recipientUserName,
            i.invite_status AS inviteStatus,
            i.invite_timestamp AS inviteTimestamp
        FROM invitation i
        JOIN chatroom c ON i.room_id = c.room_id
        JOIN users s ON i.sender_user_id = s.user_id
        JOIN users r ON i.recipient_user_id = r.user_id
        WHERE i.recipient_user_id = :userId
        AND i.invite_status = :inviteStatus
        ORDER BY i.invite_timestamp DESC
    """, nativeQuery = true)
    List<InvitationDTO> findReceivedInvitationsByStatus(
            @Param("userId") Long userId,
            @Param("inviteStatus") char inviteStatus);


    @Transactional
    @Modifying
    @Query(value = "UPDATE invitation SET invite_status = :newInviteStatus" +
            " WHERE invite_id = :inviteId AND recipient_user_id = :recipientUserId")
    int updateInviteStatusByInviteId(
            @Param("newInviteStatus") Character newInviteStatus,
            @Param("inviteId") Long inviteId,
            @Param("recipientUserId") Long recipientUserId
    );

}
