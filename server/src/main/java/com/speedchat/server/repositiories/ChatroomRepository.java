package com.speedchat.server.repositiories;

import com.speedchat.server.models.entities.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE chatroom SET room_name = :newRoomName WHERE room_id = :roomId AND host_id = :userId", nativeQuery = true)
    int updateRoomNameByRoomId(
            @Param("newRoomName") String newRoomName,
            @Param("roomId") Long roomId,
            @Param("userId") Long userId
    );

    @Query(value = "UPDATE chatroom SET room_status = :newRoomStatus WHERE room_id = :roomId and host_id = :userId", nativeQuery = true)
    int deactivateRoomByRoomIdAndUserId(
            @Param("newRoomStatus") Character newRoomStatus,
            @Param("roomId") Long roomId,
            @Param("userId") Long userId
    );
}
