package com.speedchat.server.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePayload {
    private HttpStatus httpStatus;
    private String message;
    private Object data;
}
