package com.springadvanced.taskmanager.service;
import com.springadvanced.taskmanager.dto.TodoRequestDTO;
import com.springadvanced.taskmanager.dto.TodoResponseDTO;
import com.springadvanced.taskmanager.entity.TodoEntity;
import com.springadvanced.taskmanager.entity.TodoStatus;
import com.springadvanced.taskmanager.exception.TodoNotFoundException;
import com.springadvanced.taskmanager.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  //enable mockito annotations
public  class TodoServiceTest
{
     @Mock
     private TodoRepository todoRepository;
     @InjectMocks
     private TodoService todoService;

    @Test
    public void getTodoById_ShouldReturnValidTodo_WhenExists()
    {
        //1. Get the data ready
        Long todoId = 1L;
        TodoEntity mockEntity = new TodoEntity();
        mockEntity.setId(todoId);
        mockEntity.setTitle("test");
        mockEntity.setDescription("desc");

        //2. Prepare mock behaviour
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(mockEntity));

        //3. (Act or call) the method
        TodoResponseDTO result = todoService.getTodoById(todoId);

        //4. (Assert of check) the method
        assertNotNull(result);
        assertEquals(mockEntity.getId(), result.getId());
        assertEquals(mockEntity.getTitle(), result.getTitle());
        assertEquals(mockEntity.getStatus(), result.getStatus());
        verify(todoRepository,times(1)).findById(todoId);

        System.out.println("'Get Todo by id' Test successfull");
    }

    @Test
    public void getTodoById_ShouldReturnNull_WhenNotFound()
    {
        //Set Data
        Long todoId = 99L;

        //Prepare mock behaviour
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        //(Act or call)
        assertThrows(TodoNotFoundException.class, () -> {todoService.getTodoById(todoId);});

        //Verify
        verify(todoRepository,times(1)).findById(todoId);
        System.out.println("'Id Not Found' Test successfull");
    }
}