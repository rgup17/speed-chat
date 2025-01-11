package com.speedchat.server.repositiories;

import com.speedchat.server.models.dto.ParticipantDTO;
import com.speedchat.server.models.entities.UserChatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserChatroomRepository extends JpaRepository<UserChatroom, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_chatroom WHERE user_id = :userId AND room_id = :roomId", nativeQuery = true)
    int exitRoomByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId);

    @Query(value = """
        SELECT u.user_id, u.username, u.email
        FROM users u 
        JOIN user_chatroom uc ON u.user_id = uc.user_id 
        WHERE uc.room_id = :roomId
        """, nativeQuery = true)
    List<ParticipantDTO> findParticipantsByRoomId(@Param("roomId") Long roomId);

}
