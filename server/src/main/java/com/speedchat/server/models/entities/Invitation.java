package com.speedchat.server.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inviteId;
    private Long senderUserId;
    private Long recipientUserId;
    private Long roomId;
    private Character inviteStatus;
    private Long inviteTimestamp;

    public Invitation(Long senderUserId, Long recipientUserId, Long roomId) {
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.roomId = roomId;
        this.inviteStatus = PENDING;
        this.inviteTimestamp = Instant.now().toEpochMilli();
    }

    public static final Character PENDING = 'P';
    public static final Character ACCEPTED = 'A';
    public static final Character REJECTED = 'R';
}
