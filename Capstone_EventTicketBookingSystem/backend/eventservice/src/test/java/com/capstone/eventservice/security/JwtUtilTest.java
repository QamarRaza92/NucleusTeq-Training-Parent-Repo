package com.capstone.eventservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret = "mySuperSecretKey123456789012345678901234567890";
    private Long expiration = 1800000L; // 30 minutes

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Since JwtUtil uses @Value, we need to manually set them for test
        setField(jwtUtil, "secret", secret);
        setField(jwtUtil, "expiration", expiration);
    }

    // Helper to set private fields for testing only.
    private void setField(Object obj, String fieldName, Object value) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateToken_ShouldReturnValidToken() {
        String email = "test@example.com";
        String role = "CUSTOMER";
        Long userId = 4L;

        String token = jwtUtil.generateToken(email, role);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        String email = "test@example.com";
        String role = "CUSTOMER";
        Long userId = 4L;

        String token = jwtUtil.generateToken(email, role);
        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.string";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken_ReturnsFalse() {
        // Create a token that expires immediately
        String email = "test@example.com";
        String role = "CUSTOMER";
        Long userId = 4L;

        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        
        String expiredToken = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis() - 60000)) // 1 minute ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // expired
                .signWith(key)
                .compact();

        boolean isValid = jwtUtil.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void testExtractEmail_ShouldReturnCorrectEmail() {
        String email = "test@example.com";
        String role = "CUSTOMER";
        Long userId = 4L;

        String token = jwtUtil.generateToken(email, role);
        String extractedEmail = jwtUtil.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void testExtractRole_ShouldReturnCorrectRole() {
        String email = "test@example.com";
        String role = "CUSTOMER";
        Long userId = 4L;

        String token = jwtUtil.generateToken(email, role);
        String extractedRole = jwtUtil.extractRole(token);

        assertEquals(role, extractedRole);
    }
}