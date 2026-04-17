package com.springadvanced.taskmanager.service;

import org.springframework.stereotype.Service;
import com.springadvanced.taskmanager.repository.TodoRepository;
import java.util.List;
import java.time.LocalDateTime;
import com.springadvanced.taskmanager.entity.Todo;
import com.springadvanced.taskmanager.dto.TodoRequestDTO;
import com.springadvanced.taskmanager.dto.TodoResponseDTO;
@Service 
public class TodoService
{
    private final TodoRepository todoRepository;
    //Constructor Injection of Repository 
    public TodoService(TodoRepository todoRepository)
    {
        this.todoRepository = todoRepository;
    }


    //Insert new task via DTO
    public TodoResponseDTO createTodo(TodoRequestDTO dto)
    {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus("PENDING");
        todo.setCreatedAt(LocalDateTime.now());
        Todo saved = todoRepository.save(todo);
        return convertToDTO(saved);
    }
    public TodoResponseDTO convertToDTO(Todo todo)
    {
        TodoResponseDTO dto = new TodoResponseDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setStatus(todo.getStatus());
        return dto;
    }


    //Get all tasks via DTO
    public List<TodoResponseDTO> getAllTodos()
    {
        return todoRepository.findAll().stream().map(this::convertToDTO).toList();
    }
    


    //Get task by id via DTO
    public TodoResponseDTO getTodoById(Long id)
    {
        Todo todo = todoRepository.findById(id).orElse(null);
        return (todo != null) ? convertToDTO(todo):null;
    }



    //Delete a task via DTO
    public void deleteTodo(Long id)
    {
        todoRepository.deleteById(id);
    }



    //Update a tak via DTO
    public TodoResponseDTO updateTodo(Long id, TodoRequestDTO  dto)
    {
        Todo existing = todoRepository.findById(id).orElse(null);

        if(existing!=null)
        {
            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            
            Todo updated = todoRepository.save(existing);
            return convertToDTO(updated);
        }
        return null;
    }
}