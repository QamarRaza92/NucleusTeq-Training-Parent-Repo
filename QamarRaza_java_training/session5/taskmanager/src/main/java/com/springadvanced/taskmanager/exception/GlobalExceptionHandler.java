package com.springadvanced.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler
{
    //1. Handling the custom exception
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(TodoNotFoundException ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), 
                                                ex.getMessage(), request.getDescription(false).replace("uri=",""));
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //2. Handling exceptions coming from "@Valid"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,WebRequest request)
    {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponse error = new ErrorResponse
                                              (
                                                HttpStatus.BAD_REQUEST.value(),
                                                errorMessage,
                                                request.getDescription(false).replace("uri=","")
                                              );
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //3. Handling RuntimeException (catch all fallbacks)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse
                                               (
                                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                "INTERNAL SERVER ERROR "+ ex.getMessage(),
                                                request.getDescription(false).replace("uri=","")
                                               );
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //4. Handle Generic Exceptions 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse
                                              (
                                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                ex.getMessage(),
                                                request.getDescription(false).replace("uri=","")
                                              );
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}