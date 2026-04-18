package com.springadvanced.taskmanager.service;

import org.springframework.stereotype.Service;
import com.springadvanced.taskmanager.repository.TodoRepository;
import java.util.List;
import java.time.LocalDateTime;
import com.springadvanced.taskmanager.entity.TodoEntity;
import com.springadvanced.taskmanager.entity.TodoStatus;
import com.springadvanced.taskmanager.dto.TodoRequestDTO;
import com.springadvanced.taskmanager.dto.TodoResponseDTO;

@Service
public class TodoService 
{
    private final TodoRepository todoRepository;
    public TodoService(TodoRepository todoRepository)
    {
        this.todoRepository = todoRepository;
    }

    //Convert TodoEntity to TodoResponseDTO
    public TodoResponseDTO convertToDTO(TodoEntity todo)
    {
        TodoResponseDTO dto = new TodoResponseDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setStatus(todo.getStatus());
        return dto;
    }

    //Get all todos
    public List<TodoResponseDTO> getAllTodos()
    {
        return todoRepository.findAll().stream().map(this::convertToDTO).toList();
    }  

    //Get todo by id
    public TodoResponseDTO getTodoById(Long id)
    {
        TodoEntity todo = todoRepository.findById(id).orElse(null);
        if(todo==null)
        {
            throw new RuntimeException("Can not find Todo");
        }
        else
        {
            return convertToDTO(todo);
        }
    }


    //Create new todo 
    public TodoResponseDTO createTodo(TodoRequestDTO dto)
    {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(TodoStatus.PENDING);
        todo.setCreatedAt(LocalDateTime.now());

        TodoEntity saved = todoRepository.save(todo);
        return convertToDTO(saved);
    }

    
    //Delete a task via DTO
    public void deleteTodo(Long id)
    {
        todoRepository.deleteById(id);
    }



    //Update a task via DTO
    public TodoResponseDTO updateTodo(Long id, TodoRequestDTO  dto)
    {
        TodoEntity existing = todoRepository.findById(id).orElse(null);

        if(existing!=null)
        {
            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            
            TodoEntity updated = todoRepository.save(existing);
            return convertToDTO(updated);
        }
        else
        {
            throw new RuntimeException("Task not found!!");
        }
    }


    //Toggle task status
    public TodoResponseDTO toggleStatus(Long id)
    {
        TodoEntity existing = todoRepository.findById(id).orElse(null);

        if(existing != null)
        {
            if(existing.getStatus() == TodoStatus.PENDING)
            {
                existing.setStatus(TodoStatus.COMPLETED);
            }
            else
            {
                existing.setStatus(TodoStatus.PENDING);
            }

            TodoEntity updated = todoRepository.save(existing);
            return convertToDTO(updated);
        }

        else
        {
            throw new RuntimeException("Task not found");
        }
    }


}