package com.capstone.eventservice.exception;

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
    //1. For RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                ex.getMessage(),
                                                request.getDescription(false).replace("uri=","")
                                                );
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //2. For @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request)
    {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                "Validation Failed: "+errorMessage,
                                                request.getDescription(false).replace("uri=","")
                                                );
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //3. For all other exceptions 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                ex.getMessage(),
                                                request.getDescription(false).replace("uri=","")
                                                );
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //4. For EventNotFoundException (custom exception)
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFoundException(EventNotFoundException ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                                ex.getMessage(),
                                                request.getDescription(false).replace("uri=","")
                                                );
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //For BookingNotFoundExceptino 
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                                ex.getMessage(),
                                                request.getDescription(false).replace("uri=","")
                                                );
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //For UnauthorizedException (custom class )
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request)
    {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}