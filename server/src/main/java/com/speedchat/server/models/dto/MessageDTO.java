package com.speedchat.server.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long messageId;
    private String text;
    private Long userId;
    private String username;
    private Long roomId;
    private Long timestamp;
}
