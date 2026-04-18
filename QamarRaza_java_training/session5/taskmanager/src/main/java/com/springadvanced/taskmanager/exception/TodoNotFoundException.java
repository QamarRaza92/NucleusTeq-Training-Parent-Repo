package com.springadvanced.taskmanager.exception;

public class TodoNotFoundException extends RuntimeException
{
    public TodoNotFoundException(Long id)
    {
        super("Task with id: "+id+" not found");
    }

    public TodoNotFoundException(String message)
    {
        super(message);
    }
}