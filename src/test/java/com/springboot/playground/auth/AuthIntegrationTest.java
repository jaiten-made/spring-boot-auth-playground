package com.springboot.playground.auth;

import com.springboot.playground.auth.dto.LoginRequest;
import com.springboot.playground.auth.dto.LoginResponse;
import com.springboot.playground.auth.dto.RegisterRequest;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testEndToEndAuthFlow() throws Exception {
        // 1. Register a new user
        RegisterRequest registerRequest = new RegisterRequest("integration_user", "password123");
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // 2. Login to get JWT token
        LoginRequest loginRequest = new LoginRequest("integration_user", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        String jwtToken = loginResponse.getToken();

        // 3. Access private endpoint with the valid JWT token
        mockMvc.perform(get("/api/v1/auth/private-data")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("This is a SECURED endpoint. Your Bearer JWT token was successfully validated!"));

        // 4. Access private endpoint with an invalid/bogus token (should fail)
        mockMvc.perform(get("/api/v1/auth/private-data")
                        .header("Authorization", "Bearer bogus-token-value"))
                .andExpect(status().isUnauthorized());

        // 5. Access private endpoint with no token (should fail)
        mockMvc.perform(get("/api/v1/auth/private-data"))
                .andExpect(status().isUnauthorized());
    }
}
