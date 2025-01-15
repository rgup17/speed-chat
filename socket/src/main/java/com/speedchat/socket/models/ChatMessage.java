package com.speedchat.socket.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String type;
    private String text;
    private Long userId;
    private String username;
    private Long roomId;
    private Long timestamp;
}
