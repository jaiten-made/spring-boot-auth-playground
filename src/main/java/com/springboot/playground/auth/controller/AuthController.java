package com.springboot.playground.auth.controller;

import com.springboot.playground.auth.dto.LoginRequest;
import com.springboot.playground.auth.dto.RegisterRequest;
import com.springboot.playground.auth.dto.LoginResponse;
import com.springboot.playground.auth.dto.MessageResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
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

    @PostMapping("/auth/login")
    @Operation(summary = "Authenticate user and get JWT", description = "Authenticates user credentials and returns a Bearer JWT token if successful")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful authentication, returns JWT token", 
                     content = @Content(schema = @Schema(implementation = LoginResponse.class))),
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
            if (!authentication.isAuthenticated()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            String token = jwtUtils.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(token, "success"));

        } catch (AuthenticationException e) {
            // Credentials incorrect or account locked
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed: " + e.getMessage(), e);
        }
    }

    @PostMapping("/users")
    @Operation(summary = "Register a new user", description = "Creates a new user account with USER role")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully",
                     content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Username already taken")
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        User newUser = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                "USER"
        );
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("success", "User registered successfully!"));
    }

    @GetMapping("/auth/public-data")
    @Operation(summary = "Access public endpoint", description = "An endpoint accessible by anyone without authentication")
    @ApiResponse(responseCode = "200", description = "Successfully fetched public data",
                 content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    public ResponseEntity<MessageResponse> getPublicData() {
        return ResponseEntity.ok(new MessageResponse(
                "success",
                "This is a PUBLIC endpoint. Anyone can access this without a JWT token!"
        ));
    }

    @GetMapping("/auth/private-data")
    @Operation(summary = "Access private endpoint", description = "A secured endpoint that requires a valid JWT Bearer token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully accessed private endpoint (JWT valid)",
                     content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied / Unauthorized")
    })
    public ResponseEntity<MessageResponse> getSecuredData() {
        return ResponseEntity.ok(new MessageResponse(
                "success",
                "This is a SECURED endpoint. Your Bearer JWT token was successfully validated!"
        ));
    }
}
