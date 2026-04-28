package com.capstone.eventservice.exception;
public class BookingNotFoundException extends RuntimeException
{
    public BookingNotFoundException(Long id)
    {
        super("Bookig not found with id: "+id);
    }

    public BookingNotFoundException(String message)
    {
        super(message);
    }
}