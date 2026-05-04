package com.capstone.eventservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@ControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //1. For RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request)
    {
        String path = request.getDescription(false).replace("uri=", "");
        log.warn("RuntimeException - Path: {}, Message: {}", path, ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(),path);
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //2. For @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request)
    {
        String path = request.getDescription(false).replace("uri=", "");
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.warn("Validation failed - Path: {}, Field: {}, Message: {}", 
                 path, ex.getBindingResult().getFieldErrors().get(0).getField(), errorMessage);

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"Validation Failed: "+errorMessage,path);
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //3. For all other exceptions 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request)
    {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("Unexpected error - Path: {}, Message: {}", path, ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(),path);
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //4. For EventNotFoundException (custom exception)
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFoundException(EventNotFoundException ex, WebRequest request)
    {
        String path = request.getDescription(false).replace("uri=", "");
        log.warn("Event not found - Path: {}, Message: {}", path, ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage(),path);
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //For BookingNotFoundException
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex, WebRequest request)
    {
        String path = request.getDescription(false).replace("uri=", "");
        log.warn("Booking not found - Path: {}, Message: {}", path, ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage(),path);
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //For UnauthorizedException (custom class )
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request)
    {
        String path = request.getDescription(false).replace("uri=", "");
        log.warn("Unauthorized access - Path: {}, Message: {}", path, ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(),ex.getMessage(),path);
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}