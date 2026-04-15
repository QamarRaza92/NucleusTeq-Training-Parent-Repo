package com.springadvanced.taskmanager.service;

import org.springframework.stereotype.Service;
import com.springadvanced.taskmanager.repository.TodoRepository;
import java.util.List;
import java.time.LocalDateTime;
import com.springadvanced.taskmanager.entity.Todo;
@Service 
public class TodoService
{
    private final TodoRepository todoRepository;
    public TodoService(TodoRepository todoRepository)
    {
        this.todoRepository = todoRepository;
    }

    public Todo createTodo(Todo todo)
    {
        todo.setCreatedAt(LocalDateTime.now());
        return todoRepository.save(todo);
    }

    public List<Todo> getAllTodos()
    {
        return todoRepository.findAll();
    }

    public Todo getTodoById(Long id)
    {
        return todoRepository.findById(id).orElse(null);
    }

    public void deleteTodo(Long id)
    {
        todoRepository.deleteById(id);
    }

    public Todo updateTodo(Long id, Todo updatedTodo)
    {
        Todo existing = todoRepository.findById(id).orElse(null);

        if(existing != null)
        {
            existing.setTitle(updatedTodo.getTitle());
            existing.setDescription(updatedTodo.getDescription());
            existing.setStatus(updatedTodo.getStatus());

            return todoRepository.save(existing);
        }
        return null;
    }
}