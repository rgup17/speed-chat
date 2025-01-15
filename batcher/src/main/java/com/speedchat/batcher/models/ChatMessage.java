package com.speedchat.batcher.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    @Column(length = 5000)
    private String text;
    private Long userId;
    private Long roomId;
    private Long timestamp;

    public Message(String text, Long userId, Long roomId) {
        this.text = text;
        this.userId = userId;
        this.roomId = roomId;
    }
}
