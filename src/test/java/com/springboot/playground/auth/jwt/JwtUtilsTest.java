package com.springboot.playground.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils("lZD9iHwp8Uy2gwwkEyV5s0mZRjhwZlu3TCZkMQPFsSs=");
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtUtils.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_Valid() {
        String username = "john_doe";
        String token = jwtUtils.generateToken(username);

        assertTrue(jwtUtils.validateToken(token, username));
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String username = "john_doe";
        String token = jwtUtils.generateToken(username);

        assertFalse(jwtUtils.validateToken(token, "different_user"));
    }

    @Test
    void testExtractExpiration() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        Date expiration = jwtUtils.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
}
