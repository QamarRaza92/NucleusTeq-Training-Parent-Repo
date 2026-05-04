package com.capstone.eventservice.exception;
public class BookingNotFoundException extends RuntimeException
{
    public BookingNotFoundException(Long id)
    {
        super("Booking not found with id: "+id);
    }

    public BookingNotFoundException(String message)
    {
        super(message);
    }
}