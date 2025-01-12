package com.speedchat.server.controllers;

import com.speedchat.server.models.dto.ResponsePayload;
import com.speedchat.server.services.ChatroomService;
import com.speedchat.server.utils.RequestPayloadValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chatroom")
public class ChatroomController {

    private final Logger LOGGER = LogManager.getLogger(ChatroomController.class);
    private final RequestPayloadValidator requestPayloadValidator;
    private final ChatroomService chatroomService;

    public ChatroomController(RequestPayloadValidator requestPayloadValidator, ChatroomService chatroomService) {
        this.requestPayloadValidator = requestPayloadValidator;
        this.chatroomService = chatroomService;
    }

    @PostMapping("/create-chatroom")
    public ResponseEntity<ResponsePayload> createChatroom(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        JSONObject userData = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.createChatroomValidation(body);
            Object chatroomData = chatroomService.createChatroom(userData.getLong("userId"), (String)body.get("roomName"));
            responsePayload = new ResponsePayload(HttpStatus.CREATED, "Successfully created chatroom", chatroomData);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    @PutMapping("/update-chatroom")
    public ResponseEntity<ResponsePayload> updateChatroomName(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        JSONObject userData = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.updateChatroomValidation(body);
            chatroomService.updateChatRoomName(userData.getLong("userId"), ((Number)body.get("roomId")).longValue(), (String)body.get("roomName"));
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully updated chatroom name", null);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    public ResponseEntity<ResponsePayload> deactivateChatroom(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        JSONObject userData = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.deactivateChatroomValidation(body);
            chatroomService.deactivateChatroom(userData.getLong("userId"), ((Number) body.get("roomId")).longValue());
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully deactivated chatroom", null);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    public ResponseEntity<ResponsePayload> exitChatroom(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        JSONObject userData = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            chatroomService.leaveChatroom(userData.getLong("userId"), ((Number) body.get("roomId")).longValue());
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully exited chatroom", null);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

}
