package com.springadvanced.taskmanager.exception;
import java.time.LocalDateTime;
public class ErrorResponse
{   
    //Private variables
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    //Constructor
    public ErrorResponse(int status,String message, String path)
    {
        this.status = status;
        this.message = message; 
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    // Getters
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPath() { return path; }
    
    // Setters
    public void setStatus(int status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setPath(String path) { this.path = path; }
}