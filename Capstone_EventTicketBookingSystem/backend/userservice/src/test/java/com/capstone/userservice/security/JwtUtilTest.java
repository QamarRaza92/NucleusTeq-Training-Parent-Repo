package com.capstone.userservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret = "mySuperSecretKey123456789012345678901234567890";
    private Long expiration = 1800000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        setField(jwtUtil, "secret", secret);
        setField(jwtUtil, "expiration", expiration);
    }

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
    void testGenerateToken_ShouldReturnToken() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER", 4L);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER", 4L);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token.string"));
    }

    @Test
    void testExtractEmail() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER", 4L);
        assertEquals("test@example.com", jwtUtil.extractEmail(token));
    }

    @Test
    void testExtractRole() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER", 4L);
        assertEquals("CUSTOMER", jwtUtil.extractRole(token));
    }
}