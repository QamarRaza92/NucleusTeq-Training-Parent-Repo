package com.springadvanced.taskmanager.controller;

import com.springadvanced.taskmanager.dto.TodoRequestDTO;
import com.springadvanced.taskmanager.dto.TodoResponseDTO;
import com.springadvanced.taskmanager.service.TodoService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
// import com.springadvanced.taskmanager.service.NotificationServiceClient;
import com.springadvanced.taskmanager.exception.GlobalExceptionHandler;
import com.springadvanced.taskmanager.exception.TodoNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/todos")
public class TodoController
{
    private static final Logger log = LoggerFactory.getLogger(TodoController.class);
    private final TodoService todoService;
    public TodoController(TodoService todoService)
    {
        this.todoService = todoService;
    }

    @GetMapping //get all todos 
    public List<TodoResponseDTO> getAllTodos()
    {
        log.info("GET request accepted to fetch all users...");
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")  //get todo by id 
    public TodoResponseDTO getTodoById(@PathVariable Long id)
    {
        log.info("GET request accepted to fetch user: {}",id);
        return todoService.getTodoById(id);
    }

    @PostMapping //(post or insert) new todo 
    public TodoResponseDTO createTodo(@RequestBody @Valid TodoRequestDTO dto)
    {
        log.info("POST request accepted to insert new user");
        return todoService.createTodo(dto);
    }

    @DeleteMapping("/{id}")  //delete todo by id 
    public void deleteTodo(@PathVariable Long id)
    {
        log.info("DELETE request accepted to delete user: {}",id);
        todoService.deleteTodo(id);
    }

    @PutMapping("{id}")  //(put or update) todo by id
    public TodoResponseDTO updateTodo(@PathVariable Long id, @RequestBody @Valid TodoRequestDTO dto)
    {
        log.info("PUT request accepted to update user: {}",id);
        return todoService.updateTodo(id,dto);
    }

    @PatchMapping("/toggleStatus/{id}")  //toggle status of todo 
    public TodoResponseDTO toggleStatus(@PathVariable Long id)
    {
        log.info("PATCH request accepted to update user: {}",id);
        return todoService.toggleStatus(id);
    }
}