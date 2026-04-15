package com.springadvanced.taskmanager.service;

import org.springframework.stereotype.Service;
import com.springadvanced.taskmanager.repository.TodoRepository;
@Service 
public class TodoService
{
    private final TodoRepository todorepository;
    public TodoService(TodoRepository todorepository)
    {
        this.todorepository = todorepository;
    }

    public Todo createTodo(Todo todo)
    {
        todo.setCreatedAt(LocalDateTime.now());
        return todorepository.save(todo);
    }

    public List<Todo> getAllTodos()
    {
        todorepository.findAll();
    }

    public Todo getTodoById(Long id)
    {
        return todorepository.findById(id).orElse(null);
    }

    public void deleteTodo(Long id)
    {
        todorepository.deleteById(id);
    }
}