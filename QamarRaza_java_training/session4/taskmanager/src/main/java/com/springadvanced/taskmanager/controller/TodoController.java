package com.springadvanced.taskmanager.controller;

import org.springframework.web.bind.annotation.*;
import com.springadvanced.taskmanager.service.TodoService;
import com.springadvanced.taskmanager.entity.Todo;
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

    @GetMapping
    public List<Todo> getAllTodos()
    {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    public Todo getTodoById(@PathVariable Long id)
    {
        return todoService.getTodoById(id);
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo)
    {
        return todoService.createTodo(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id)
    {
        todoService.deleteTodo(id);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todo)
    {
        return todoService.updateTodo(id,todo);
    }
}