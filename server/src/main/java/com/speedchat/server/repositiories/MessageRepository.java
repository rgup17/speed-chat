package com.speedchat.server.repositiories;

import com.speedchat.server.models.dto.MessageDTO;
import com.speedchat.server.models.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = """
        SELECT 
            m.message_id AS messageId, 
            m.text AS text, 
            m.user_id AS userId, 
            u.user_name AS username, 
            m.room_id AS roomId, 
            m.timestamp AS timestamp 
        FROM message m 
        JOIN users u ON m.user_id = u.user_id 
        WHERE m.room_id = :roomId 
        ORDER BY m.timestamp 
        LIMIT :pageLimit 
        OFFSET (:pageNumber - 1) * :pageLimit
    """, nativeQuery = true)
    List<MessageDTO> findMessagesByRoomId(
            @Param("roomId") Long roomId,
            @Param("pageLimit") int pageLimit,
            @Param("pageNumber") int pageNumber);
}
