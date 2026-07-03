package com.bookrealm.ai.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilsTest {
    private static final String JWT_SECRET = "dev-only-secret-please-change-in-production-0123456789abcdef";

    @Test
    void blankSecretShouldThrowConfiguredMessage() {
        assertThatThrownBy(() -> new JwtUtils(" "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("jwt.secret or JWT_SECRET must be configured");
    }

    @Test
    void validTokenShouldParseSubjectAndRole() {
        String token = Jwts.builder()
                .subject("42")
                .claim("role", 1)
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        AuthenticatedUser user = new JwtUtils(JWT_SECRET).parse(token);

        assertThat(user.userId()).isEqualTo(42L);
        assertThat(user.role()).isEqualTo(1);
        assertThat(user.isAdmin()).isTrue();
    }
}
