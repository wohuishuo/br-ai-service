package com.bookrealm.ai.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtils {
    private final SecretKey secretKey;

    public JwtUtils(@Value("${jwt.secret:${JWT_SECRET:}}") String secret) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("jwt.secret or JWT_SECRET must be configured");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public AuthenticatedUser parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.valueOf(claims.getSubject());
        Integer role = claims.get("role", Integer.class);
        return new AuthenticatedUser(userId, role);
    }
}
