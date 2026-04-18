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
@RestController
@RequestMapping("/todos")
public class TodoController
{
    private final TodoService todoService;
    public TodoController(TodoService todoService)
    {
        this.todoService = todoService;
    }

    @GetMapping //get all todos 
    public List<TodoResponseDTO> getAllTodos()
    {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")  //get todo by id 
    public TodoResponseDTO getTodoById(@PathVariable Long id)
    {
        return todoService.getTodoById(id);
    }

    @PostMapping //(post or insert) new todo 
    public TodoResponseDTO createTodo(@RequestBody @Valid TodoRequestDTO dto)
    {
        return todoService.createTodo(dto);
    }

    @DeleteMapping("/{id}")  //delete todo by id 
    public void deleteTodo(@PathVariable Long id)
    {
        todoService.deleteTodo(id);
    }

    @PutMapping("{id}")  //(put or update) todo by id
    public TodoResponseDTO updateTodo(@PathVariable Long id, @RequestBody @Valid TodoRequestDTO dto)
    {
        return todoService.updateTodo(id,dto);
    }

    @PatchMapping("/toggleStatus/{id}")  //toggle status of todo 
    public TodoResponseDTO toggleStatus(@PathVariable Long id)
    {
        return todoService.toggleStatus(id);
    }
}