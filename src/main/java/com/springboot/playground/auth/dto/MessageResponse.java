package com.springboot.playground.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a message response")
public class MessageResponse {
    @Schema(description = "The status of the operation", example = "success")
    private String status;

    @Schema(description = "A descriptive message detailing the operation result", example = "Operation completed successfully")
    private String message;

    public MessageResponse() {}

    public MessageResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
