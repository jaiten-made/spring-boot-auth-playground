package com.springboot.playground.auth.controller;

import com.springboot.playground.auth.dto.LoginRequest;
import tools.jackson.databind.ObjectMapper;
import com.springboot.playground.auth.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.springboot.playground.auth.config.SecurityConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.BeforeEach;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @MockitoBean
    private org.springframework.boot.h2console.autoconfigure.H2ConsoleProperties h2ConsoleProperties;

    @BeforeEach
    void setUp() throws Exception {
        when(h2ConsoleProperties.getPath()).thenReturn("/h2-console");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void testPublicEndpoint_Success() throws Exception {
        mockMvc.perform(get("/api/v1/auth/public-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("This is a PUBLIC endpoint. Anyone can access this without a JWT token!"));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password");

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtils.generateToken("testuser")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void testLogin_Failure_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPrivateEndpoint_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/private-data"))
                .andExpect(status().isUnauthorized());
    }
}
