package com.springadvanced.taskmanager.service;

import org.springframework.stereotype.Service;
import com.springadvanced.taskmanager.repository.TodoRepository;
import java.util.List;
import java.time.LocalDateTime;
import com.springadvanced.taskmanager.entity.TodoEntity;
import com.springadvanced.taskmanager.entity.TodoStatus;
import com.springadvanced.taskmanager.dto.TodoRequestDTO;
import com.springadvanced.taskmanager.dto.TodoResponseDTO;
import com.springadvanced.taskmanager.exception.TodoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TodoService 
{
    private static final Logger log = LoggerFactory.getLogger(TodoService.class);
    private final TodoRepository todoRepository;
    private final NotificationServiceClient notify;
    public TodoService(TodoRepository todoRepository,NotificationServiceClient notify)
    {
        this.todoRepository = todoRepository;
        this.notify = notify;
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
            log.warn("Cannot find Todo: {}",id);
            throw new TodoNotFoundException(id);
        }
        else
        {
            log.info("Fetching Todo: {}",id);
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
        log.info("New Todo with id:{} and title:'{}' created",saved.getId(),saved.getTitle());
        notify.sendNotification("Created new Todo with id:"+saved.getId());
        return convertToDTO(saved);
    }


    //Delete a task via DTO
    public void deleteTodo(Long id)
    {
        if(!todoRepository.existsById(id))
        {
            log.warn("Todo: {} does not exist",id);
            throw new TodoNotFoundException(id);
        }
        else
        {
            todoRepository.deleteById(id);
            log.info("Deleted Todo: {}",id);
        }
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
            log.info("Updated Todo: {}",id);
            return convertToDTO(updated);
        }
        else
        {
            log.warn("Todo: {} not found",id);
            throw new TodoNotFoundException(id);
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
            log.info("Updated Todo: {}'s status",id);
            return convertToDTO(updated);
        }

        else
        {
            log.info("Todo: {} not found",id);
            throw new TodoNotFoundException(id);
        }
    }
}