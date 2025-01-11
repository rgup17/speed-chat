package com.speedchat.server.controllers;

import com.speedchat.server.models.dto.ResponsePayload;
import com.speedchat.server.services.AuthenticationService;
import com.speedchat.server.utils.RequestPayloadValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.util.Pair;


import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("authentication")
public class AuthenticationController {
    private final RequestPayloadValidator requestPayloadValidator;
    private final AuthenticationService authenticationService;

    public AuthenticationController(RequestPayloadValidator requestPayloadValidator, AuthenticationService authenticationService) {
        this.requestPayloadValidator = requestPayloadValidator;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ResponsePayload> sendOTP(@RequestBody Map<String, Object> body) {
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.sendOTPValidation(body);
            String email = (String) body.get("email");
            authenticationService.sendOTP(email);
            responsePayload = new ResponsePayload(HttpStatus.OK, "OTP sent to " + email + " successfully.", null);
        } catch (Exception e) {
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponsePayload> verifyOTP(@RequestBody Map<String, Object> body) {
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.verifyOTPValidation(body);
            Pair<Object, String> data = authenticationService.verifyOTP((String) body.get("email"), (String) body.get("OTP"));
            responsePayload = new ResponsePayload(
                    data.getSecond().equals("E") ? HttpStatus.OK : HttpStatus.CREATED,
                    "Email verified successfully",
                    data.getFirst());
        } catch (Exception e) {
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

    @PutMapping("/save-username")
    public ResponseEntity<ResponsePayload> saveUsername(@RequestBody Map<String, Object> body) {
        ResponsePayload responsePayload;
        try {
            requestPayloadValidator.saveUsernameValidation(body);
            Object data = authenticationService.saveUsername((Long) body.get("userId"), (String) body.get("username"), (String) body.get("email"));
            responsePayload = new ResponsePayload(HttpStatus.OK, "Successfully updated username", data);
        } catch (Exception e) {
            responsePayload = new ResponsePayload(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
        return ResponseEntity.status(responsePayload.getHttpStatus()).body(responsePayload);
    }

}
