package com.springadvanced.taskmanager.service;

import org.springframework.stereotype.Service;
import com.springadvanced.taskmanager.repository.TodoRepository;
@Service 
public class TodoService
{
    private final TodoRepository todorepository;
    public TodoService(TodoRepository todorepository)
    {
        
    }
}