package com.capstone.userservice.dto;
public class ErrorResponseDTO {
    private String error;

    public ErrorResponseDTO(String error) {
        this.error = error;
    }

    // getter
    public String getError() {
        return error;
    }
}