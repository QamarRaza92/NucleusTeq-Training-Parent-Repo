package com.springadvanced.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHanlder
{
    //1. Handling the customer exception
    @ExceptionHanlder(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(TodoNotFoundException ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), 
                                                ex.getMessage(), request.getDescription(false).replace("uri=",""));
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
}