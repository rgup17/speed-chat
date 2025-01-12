package com.speedchat.server.utils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RequestPayloadValidator {

    public void sendOTPValidation(Map<String, Object> body) throws InvalidRequestBodyException {
        // check if email is provided
        if (!body.containsKey("email")) throw new InvalidRequestBodyException("User's email is required in request body");
        // validate email
        validateEmail((String) body.get("email"));
    }

    public void verifyOTPValidation(Map<String, Object> body) throws InvalidRequestBodyException {
        sendOTPValidation(body);
        if (!body.containsKey("OTP")) throw new InvalidRequestBodyException("OTP is required in request body");
        String OTP = (String) body.get("OTP");
        if (OTP == null || OTP.length() != 6 || !OTP.matches("\\d{6}")) throw new InvalidRequestBodyException("Invalid OTP sent");
    }

    public void saveUsernameValidation(Map<String, Object> body) throws InvalidRequestBodyException {
        if (!body.containsKey("username")) throw new InvalidRequestBodyException("username is required in request body");
        String username = (String) body.get("username");
        if (username == null || username.isEmpty()) throw new InvalidRequestBodyException("Invalid username");
    }

    private void validateEmail(String email) throws InvalidRequestBodyException {
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()) throw new InvalidRequestBodyException("Invalid Email Address");
    }

    public void deactivateChatroomValidation(Map<String, Object> body) throws InvalidRequestBodyException {
        if(!body.containsKey("roomId")) throw new InvalidRequestBodyException("roomId is required in request body");
        try {
            ((Number) body.get("roomId")).longValue();
        } catch(Exception e) {
            throw new InvalidRequestBodyException(e.getMessage());
        }
    }

    public void inviteUserByEmailValidation(Map<String, Object> body) throws InvalidRequestBodyException {
        if(!body.containsKey("recipientEmail")) throw new InvalidRequestBodyException("recipientEmail is required in request body");
        validateEmail((String) body.get("recipientEmail"));
        this.deactivateChatroomValidation(body);
    }

    public void updateInviteValidation(Map<String, Object> body) throws InvalidRequestBodyException {
        if(!body.containsKey("inviteId")) throw new InvalidRequestBodyException("inviteId is required field");
        if(!body.containsKey("inviteStatus")) throw new InvalidRequestBodyException("inviteStatus is required field");

        try {
            ((Number) body.get("inviteId")).longValue();
        } catch(Exception e) {
            throw new InvalidRequestBodyException(e.getMessage());
        }
    }

    public static class InvalidRequestBodyException extends Exception {
        public InvalidRequestBodyException(String message) {
            super(message);
        }
    }

}
