package com.springadvanced.taskmanager.controller;

import org.springframework.web.bind.annotation.*;
import com.springadvanced.taskmanager.service.TodoService;
import com.springadvanced.taskmanager.dto.TodoRequestDTO;
import com.springadvanced.taskmanager.dto.TodoResponseDTO;
import java.util.List;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/todos")
public class TodoController
{
    private final TodoService todoService;
    public TodoController(TodoService todoService)
    {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponseDTO> getAllTodos()
    {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    public TodoResponseDTO getTodoById(@PathVariable Long id)
    {
        return todoService.getTodoById(id);
    }

    @PostMapping
    public TodoResponseDTO createTodo(@RequestBody @Valid TodoRequestDTO dto)
    {
        return todoService.createTodo(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id)
    {
        todoService.deleteTodo(id);
    }

    @PutMapping("/{id}")
    public TodoResponseDTO updateTodo(@PathVariable Long id, @RequestBody @Valid TodoRequestDTO dto)
    {
        return todoService.updateTodo(id,dto);
    }

    @PutMapping("/{id}/status")
    public TodoResponseDTO toggleStatus(@PathVariable Long id)
    {
        return todoService.toggleStatus(id);
    }
}