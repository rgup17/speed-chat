package com.speedchat.server.controllers;

import com.speedchat.server.models.dto.ResponsePayload;
import com.speedchat.server.services.InvitationService;
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
@RequestMapping("/invitation")
public class InvitationController {

    private final Logger LOGGER = LogManager.getLogger(InvitationController.class);
    private final InvitationService invitationService;
    private final RequestPayloadValidator requestPayloadValidator;

    public InvitationController(InvitationService invitationService, RequestPayloadValidator requestPayloadValidator) {
        this.invitationService = invitationService;
        this.requestPayloadValidator = requestPayloadValidator;
    }

    @GetMapping("/sent-invitations/{inviteStatus}")
    public ResponseEntity<ResponsePayload> getSentInvitations(@PathVariable String inviteStatus, HttpServletRequest request) {
        JSONObject userData = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            Object data = invitationService.getSentInvitationsByInvitationStatus(userData.getLong("userId"), inviteStatus.charAt(0));
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully fetched sent invitations", data);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    @GetMapping("/received-invitations/{inviteStatus")
    public ResponseEntity<ResponsePayload> getReceivedInvitations(@PathVariable String inviteStatus, HttpServletRequest request) {
        JSONObject userData = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            Object data = invitationService.getReceivedInvitationsByInvitationStatus(userData.getLong("userId"), inviteStatus.charAt(0));
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully fetched received invitations", data);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    @PostMapping("/invite-user")
    public ResponseEntity<ResponsePayload> inviteUserByUserEmail(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        JSONObject userData = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.inviteUserByEmailValidation(body);
            invitationService.inviteUserByEmail(userData.getLong("userId"), (String) body.get("recipientEmail"), (Long) body.get("roomId") );
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully invited " + (String) body.get("recipientEmail"), null);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    @PutMapping("/update-invite-status")
    public ResponseEntity<ResponsePayload> updateInviteStatus(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        JSONObject user = (JSONObject) request.getAttribute("userData");
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.updateInviteValidation(body);
            invitationService.updateInvitationStatus(
                    user.getLong("userId"),
                    ((Number) body.get("inviteId")).longValue(),
                    ((String) body.get("inviteStatus")).charAt(0)
            );
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully updated invite status", null);
        } catch(Exception e) {
            LOGGER.info(e.getMessage());
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

}
