package com.springadvanced.taskmanager.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoNotFoundExceptionTest
{
    @Test
    void testExceptionMessage_WithId()
    {
        // ARRANGE
        Long todoId = 99L;

        // ACT
        TodoNotFoundException exception = new TodoNotFoundException(todoId);

        // ASSERT
        assertEquals("Task with id: 99 not found", exception.getMessage());

        System.out.println("'TodoNotFoundException' Test successfull");
    }

    @Test
    void testExceptionMessage_WithDifferentId()
    {
        // ARRANGE
        Long todoId = 1L;

        // ACT
        TodoNotFoundException exception = new TodoNotFoundException(todoId);

        // ASSERT
        assertEquals("Task with id: 1 not found", exception.getMessage());

        System.out.println("'TodoNotFoundException with different' Test successfull");
    }

    @Test
    void testException_WithStringMessage()
    {
        // ARRANGE
        String customMessage = "Custom error message";

        // ACT
        TodoNotFoundException exception = new TodoNotFoundException(customMessage);

        // ASSERT
        assertEquals(customMessage, exception.getMessage());


        System.out.println("'TodoNotFoundException' Test successfull");
    }
}