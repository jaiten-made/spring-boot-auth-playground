package com.springboot.playground.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing the successful authentication response containing the JWT token")
public class LoginResponse {
    @Schema(description = "The JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "The status of the response", example = "success")
    private String status;

    public LoginResponse() {}

    public LoginResponse(String token, String status) {
        this.token = token;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
