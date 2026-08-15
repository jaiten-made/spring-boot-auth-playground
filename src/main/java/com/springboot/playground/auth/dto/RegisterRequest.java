package com.springboot.playground.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a user registration request")
public class RegisterRequest {
    @Schema(description = "The username for the new user", example = "newuser", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "The password for the new user", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password) {
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
