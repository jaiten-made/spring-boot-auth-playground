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

    @Test
    void testValidateToken_ExpiredTokenThrowsException() {
        // Create an expired token manually using jjwt
        String username = "testuser";
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 10000); // 10 seconds ago

        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .subject(username)
                .issuedAt(expiredDate)
                .expiration(expiredDate)
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("lZD9iHwp8Uy2gwwkEyV5s0mZRjhwZlu3TCZkMQPFsSs=".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            jwtUtils.validateToken(expiredToken, username);
        });
    }

    @Test
    void testValidateToken_InvalidSignatureThrowsException() {
        String username = "testuser";
        // Sign with a different key
        String tokenWithDifferentKey = io.jsonwebtoken.Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("differentKeySecurityTokenPlaceholder12345!".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();

        assertThrows(io.jsonwebtoken.security.SignatureException.class, () -> {
            jwtUtils.validateToken(tokenWithDifferentKey, username);
        });
    }

    @Test
    void testValidateToken_MalformedTokenThrowsException() {
        String malformedToken = "not.a.valid.jwt";

        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> {
            jwtUtils.validateToken(malformedToken, "testuser");
        });
    }
}
