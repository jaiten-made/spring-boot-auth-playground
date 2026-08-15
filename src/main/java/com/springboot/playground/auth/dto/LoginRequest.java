package com.springboot.playground.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a user login request")
public class LoginRequest {
    @Schema(description = "The username of the user trying to authenticate", example = "testuser", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "The password of the user", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
