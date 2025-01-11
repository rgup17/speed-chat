package com.speedchat.server.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chatroom")
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String roomName;
    private Long hostId;
    private Long creationTimestamp;
    private Long deletionTimestamp;

    @Column(columnDefinition = "CHAR(1) DEFAULT 'A'")
    private char roomStatus;

    @Transient
    private Long participantCount;

    public Chatroom(String roomName, Long hostId) {
        this.roomName = roomName;
        this.hostId = hostId;
        this.creationTimestamp = Instant.now().toEpochMilli();
        this.roomStatus = 'A';
    }
}
