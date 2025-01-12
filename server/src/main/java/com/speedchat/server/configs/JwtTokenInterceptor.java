package com.speedchat.server.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speedchat.server.models.dto.ResponsePayload;
import com.speedchat.server.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.OutputStream;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {
    private final AuthenticationService authService;
    private final ObjectMapper objectMapper;

    public JwtTokenInterceptor(AuthenticationService authService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        OutputStream out = response.getOutputStream();
        ResponsePayload responsePayload = new ResponsePayload();

        try {
            if(token == null || token.trim().isEmpty()) throw new Exception("Access token is not present");
            token = token.trim().replaceFirst("(?i)^Bearer ", "");  // Removing the prefix safely
            JSONObject userData = authService.validateUserToken(token);

            if(userData.getBoolean("isAuthenticated")) {
                request.setAttribute("userData", userData);
                return true;
            } else {
                throw new Exception("Access token is either expired or invalid");
            }
        } catch(Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responsePayload.setHttpStatus(HttpStatus.UNAUTHORIZED);
            responsePayload.setMessage(e.getMessage());
            objectMapper.writeValue(out, responsePayload);
            return false;
        }
    }
}
