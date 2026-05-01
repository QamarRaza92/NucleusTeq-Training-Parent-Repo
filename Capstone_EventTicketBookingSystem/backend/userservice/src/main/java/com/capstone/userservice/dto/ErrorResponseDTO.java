package com.capstone.userservice.dto;
public class ErrorResponseDTO {
    private String error;
    private int status;

    public ErrorResponseDTO(String error,int status) {
        this.error = error;
        this.status = status;
    }

    // getter
    public String getError() {
        return error;
    }
    public String getStatus() {
        return status;
    }
}