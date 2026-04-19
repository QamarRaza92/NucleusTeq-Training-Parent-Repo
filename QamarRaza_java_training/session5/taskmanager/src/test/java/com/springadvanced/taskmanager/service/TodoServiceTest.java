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

    @Test
    public void createTodo_ShouldCreate_NewTodo()
    {
        // Create mock data
        TodoRequestDTO dto = new TodoRequestDTO();
        dto.setTitle("New Task");
        dto.setDescription("Testing insertion of new task");

        TodoEntity entityToSave = new TodoEntity();
        entityToSave.setTitle("New Task");
        entityToSave.setDescription("Testing insertion of new task");
        entityToSave.setStatus(TodoStatus.PENDING);

        TodoEntity savedEntity = new TodoEntity();
        savedEntity.setId(1L);
        savedEntity.setTitle("New Task");
        savedEntity.setDescription("Testing insertion of new task");
        savedEntity.setStatus(TodoStatus.PENDING);
        savedEntity.setCreatedAt(LocalDateTime.now());

        //Define behaviour
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(savedEntity);

        //Act
        TodoResponseDTO result = todoService.createTodo(dto);

        //Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Task", result.getTitle());
        assertEquals(TodoStatus.PENDING, result.getStatus());

        //Verify
        verify(todoRepository,times(1)).save(any(TodoEntity.class));
        System.out.println("'New Todo creation' Test successful");
    }


    @Test
    public void deleteTodo_ShouldDelete_WhenExists()
    {
        //Create data
        Long todoId = 1L;

        //Define behaviour
        when(todoRepository.existsById(todoId)).thenReturn(true);
        doNothing().when(todoRepository).deleteById(todoId);

        //Act
        todoService.deleteTodo(todoId);

        //Verify
        verify(todoRepository,times(1)).deleteById(todoId);
        verify(todoRepository,times(1)).existsById(todoId);
        System.out.println("'New Todo deletion when todo exists' Test successful");
    }

    @Test
    void deleteTodo_WhenNotExists_ShouldThrowException()
    {
        // ARRANGE
        Long todoId = 99L;

        //Define behaviour
        when(todoRepository.existsById(todoId)).thenReturn(false);

        // ACT & ASSERT
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.deleteTodo(todoId);
        });

        // VERIFY - deleteById kabhi call nahi hona chahiye
        verify(todoRepository, never()).deleteById(anyLong());
        System.out.println("'New Todo deletion when not exists' Test successful");
    }

    @Test
    void updateTodo_WhenTodoExists_ShouldUpdate_AndReturnTodo()
    {
        //ARRANGE
        Long todoId = 1L;
        TodoRequestDTO newDTO = new TodoRequestDTO();
        newDTO.setTitle("Updated Title");
        newDTO.setDescription("Updated description");

        TodoEntity existing = new TodoEntity();
        existing.setId(todoId);
        existing.setTitle("Old Title");
        existing.setDescription("Old description");
        existing.setStatus(TodoStatus.PENDING);

        TodoEntity updatedEntity = new TodoEntity();
        updatedEntity.setId(todoId);
        updatedEntity.setTitle("Updated Title");
        updatedEntity.setDescription("Updated description");
        updatedEntity.setStatus(TodoStatus.PENDING);
        
        //Define behaviour 
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existing));
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(updatedEntity);

        //Act
        TodoResponseDTO result = todoService.updateTodo(todoId,newDTO);

        //Assert 
        assertNotNull(result);
        assertEquals(todoId,result.getId());
        assertEquals("Updated Title",result.getTitle());
        assertEquals(TodoStatus.PENDING,result.getStatus());

        //verify - It should update and existing todo 
        verify(todoRepository,times(1)).findById(todoId);
        verify(todoRepository,times(1)).save(any(TodoEntity.class));
        System.out.println("'Update TODO When Exists' Test successful");
    }

    @Test 
    public void updateTodo_WhenTodoDoesNotExist_ShouldThrowException()
    {
        //Create Data
        Long todoId = 99L;
        TodoRequestDTO newDTO = new TodoRequestDTO();


        //Define Behaviour 
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(TodoNotFoundException.class,() -> {todoService.updateTodo(todoId,newDTO);});

        //Verify 
        verify(todoRepository,times(1)).findById(todoId);
        verify(todoRepository,never()).save(any(TodoEntity.class));
        System.out.println("'Update todo when does not exist' Test successful");
    }
}