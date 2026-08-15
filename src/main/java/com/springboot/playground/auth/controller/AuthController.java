package com.springboot.playground.auth.controller;

import com.springboot.playground.auth.dto.LoginRequest;
import com.springboot.playground.auth.dto.RegisterRequest;
import com.springboot.playground.auth.jwt.JwtUtils;
import com.springboot.playground.auth.repository.UserRepository;
import com.springboot.playground.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Endpoints for user authentication, registration, and session checking")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/auth/login")
    @Operation(summary = "Authenticate user and get JWT", description = "Authenticates user credentials and returns a Bearer JWT token if successful")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful authentication, returns JWT token"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Authenticate credentials using the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 2. If authentication is successful, generate a JWT token
            if (authentication.isAuthenticated()) {
                String token = jwtUtils.generateToken(loginRequest.getUsername());
                
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("token", token);
                responseBody.put("status", "success");
                
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

        } catch (AuthenticationException e) {
            // Credentials incorrect or account locked
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/api/auth/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with USER role")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Username already taken")
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
        }

        User newUser = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                "USER"
        );
        userRepository.save(newUser);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("message", "User registered successfully!");

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/api/auth/public")
    @Operation(summary = "Access public endpoint", description = "An endpoint accessible by anyone without authentication")
    @ApiResponse(responseCode = "200", description = "Successfully fetched public data")
    public Map<String, String> getPublicData() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "This is a PUBLIC endpoint. Anyone can access this without a JWT token!");
        return response;
    }

    @GetMapping("/api/auth/private")
    @Operation(summary = "Access private endpoint", description = "A secured endpoint that requires a valid JWT Bearer token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully accessed private endpoint (JWT valid)"),
        @ApiResponse(responseCode = "403", description = "Access denied / Unauthorized")
    })
    public Map<String, String> getSecuredData() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "This is a SECURED endpoint. Your Bearer JWT token was successfully validated!");
        return response;
    }
}
