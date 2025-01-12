package com.speedchat.server.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {
    private Long inviteId;
    private Long senderUserId;
    private String senderUsername;
    private Long recipientUserId;
    private String recipientUsername;
    private Long roomId;
    private String roomName;
    private Character inviteStatus;
    private Long inviteTimestamp;

}
