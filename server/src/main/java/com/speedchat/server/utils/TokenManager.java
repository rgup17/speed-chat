package com.speedchat.server.utils;

import com.speedchat.server.models.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class TokenManager {
    @Value("${speed-chat.jwt-secret}")
    private String jwtSecret;

    public String generateJWTToken(User user) {
        Date date = new Date();
        Date expiryDate = new Date(date.getTime() + 24 * 60 * 60 * 1000);

        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("userId", user.getUserId())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .issuedAt(date)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(getKeyInBytes()), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * If the token is valid, it returns its claims, otherwise throws exceptions.
     */
    public Claims validateJWTToken(String jwtToken) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException {
        return getClaimsFromToken(jwtToken);
    }

    private Claims getClaimsFromToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(getKeyInBytes()))
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private byte[] getKeyInBytes() {
        return Decoders.BASE64.decode(jwtSecret);
    }
}
