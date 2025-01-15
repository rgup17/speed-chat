package com.speedchat.socket.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class TokenService {
    @Value("${speed-chat.jwt-secret}")
    private String jwtSecret;

    private final Logger logger = LogManager.getLogger(TokenService.class);

    public Claims validateJWTToken(String token) {
        try {
            return this.getClaimsFromToken(token);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getKeyInBytes() {
        return Decoders.BASE64.decode(jwtSecret);
    }

    private Claims getClaimsFromToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(getKeyInBytes()))
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }
}