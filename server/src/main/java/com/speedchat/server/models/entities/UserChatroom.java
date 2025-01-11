package com.speedchat.server.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps user to its chatrooms
 */

@Entity
@Table(name = "user_chatroom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mapId;
    private Long userId;
    private Long roomId;

    public UserChatroom(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }
}
