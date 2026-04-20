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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  //enable mockito annotations
public  class TodoServiceTest
{
     @Mock
     private TodoRepository todoRepository;
     @Mock
     private NotificationServiceClient notificationClient;
     @InjectMocks
     private TodoService todoService;

     @Test
     public void convertToDTO_ShouldCovertTodoEntityIntoTodoResponseDTO()
     {
         //act
         TodoEntity todo = new TodoEntity();
         todo.setId(1L);
         todo.setTitle("Test");
         todo.setStatus(TodoStatus.PENDING);
         todo.setDescription("test description");

         //Act
         TodoResponseDTO converted = todoService.convertToDTO(todo);

         //Assert
         assertEquals(1L, converted.getId());
         assertEquals("Test", converted.getTitle());
         assertEquals(TodoStatus.PENDING, converted.getStatus());

         System.out.println("'convert to DTO' Test successfull");
     }

     @Test
     public void getAllTodos_ShouldReturnAllTodos()
     {
         //data
         List<TodoEntity> todos = new ArrayList<>();
         TodoEntity todo1 = new TodoEntity();
         todo1.setId(1L);
         todo1.setTitle("Test");
         todo1.setStatus(TodoStatus.PENDING);

         todos.add(todo1);

        //behavior
         when(todoRepository.findAll().stream().toList()).thenReturn(todos);


         //act
         List<TodoResponseDTO> result = todoService.getAllTodos();

         //assert
         assertEquals(todos.get(0).getTitle(), result.get(0).getTitle());
         assertEquals(todos.get(0).getId(), result.get(0).getId());

         //verify
         verify(todoRepository, times(1)).findAll();

         System.out.println("'GetAllTodos' Test successfull");
     }

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
    void createTodo_ShouldCreate_NewTodo()
    {
        // ARRANGE
        TodoRequestDTO requestDTO = new TodoRequestDTO();
        requestDTO.setTitle("New Task");
        requestDTO.setDescription("New Description");

        TodoEntity todoToSave = new TodoEntity();
        todoToSave.setTitle("New Task");
        todoToSave.setDescription("New Description");
        todoToSave.setStatus(TodoStatus.PENDING);
        todoToSave.setCreatedAt(LocalDateTime.now());

        TodoEntity savedTodo = new TodoEntity();
        savedTodo.setId(1L);
        savedTodo.setTitle("New Task");
        savedTodo.setDescription("New Description");
        savedTodo.setStatus(TodoStatus.PENDING);
        savedTodo.setCreatedAt(LocalDateTime.now());

        when(todoRepository.save(any(TodoEntity.class))).thenReturn(savedTodo);

        //Notification mock
        when(notificationClient.sendNotification(anyString())).thenReturn("Notification sent");

        // ACT
        TodoResponseDTO result = todoService.createTodo(requestDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Task", result.getTitle());

        // VERIFY
        verify(todoRepository, times(1)).save(any(TodoEntity.class));
        verify(notificationClient, times(1)).sendNotification(anyString());
        System.out.println("'Create Todo' Test successfull");
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

    @Test 
    void toggleStatus_WhenExistsAndStatusIsPENDING_ShouldToggleToCOMPLETED()
    {
        // ARRANGE
        Long todoId = 1L;
        
        // Existing todo (PENDING)
        TodoEntity existingTodo = new TodoEntity();
        existingTodo.setId(todoId);
        existingTodo.setTitle("Test Todo");
        existingTodo.setDescription("Test Description");
        existingTodo.setStatus(TodoStatus.PENDING);
        
        // Updated todo (COMPLETED)
        TodoEntity updatedTodo = new TodoEntity();
        updatedTodo.setId(todoId);
        updatedTodo.setTitle("Test Todo");
        updatedTodo.setDescription("Test Description");
        updatedTodo.setStatus(TodoStatus.COMPLETED);
        
        // Mock behaviour
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(updatedTodo);
        
        // ACT
        TodoResponseDTO result = todoService.toggleStatus(todoId);
        
        // ASSERT
        assertNotNull(result);
        assertEquals(todoId, result.getId());
        assertEquals(TodoStatus.COMPLETED, result.getStatus());
        
        // VERIFY
        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(any(TodoEntity.class));
        System.out.println("'Toggle Todo Status from PENDING to COMPLETED when Todo exists' Test successful");
    }
    
    // Test 2: COMPLETED → PENDING
    @Test 
    void toggleStatus_WhenExistsAndStatusIsCOMPLETED_ShouldToggleToPENDING()
    {
        // ARRANGE
        Long todoId = 1L;
        
        // Existing todo (COMPLETED)
        TodoEntity existingTodo = new TodoEntity();
        existingTodo.setId(todoId);
        existingTodo.setTitle("Test Todo");
        existingTodo.setDescription("Test Description");
        existingTodo.setStatus(TodoStatus.COMPLETED);
        
        // Updated todo (PENDING)
        TodoEntity updatedTodo = new TodoEntity();
        updatedTodo.setId(todoId);
        updatedTodo.setTitle("Test Todo");
        updatedTodo.setDescription("Test Description");
        updatedTodo.setStatus(TodoStatus.PENDING);
        
        // Mock behaviour
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(updatedTodo);
        
        // ACT
        TodoResponseDTO result = todoService.toggleStatus(todoId);
        
        // ASSERT
        assertNotNull(result);
        assertEquals(todoId, result.getId());
        assertEquals(TodoStatus.PENDING, result.getStatus());
        
        // VERIFY
        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(any(TodoEntity.class));
        System.out.println("'Toggle Todo Status from COMPLETED to PENDING when Todo exists' Test successful");
    }
    
    // Test 3: Todo not found
    @Test
    void toggleStatus_WhenNotFound_ShouldThrowTodoNotFoundException()
    {
        // ARRANGE
        Long todoId = 99L;
        
        // Mock behaviour - empty return
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.toggleStatus(todoId);
        });
        
        // VERIFY
        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, never()).save(any(TodoEntity.class));
        System.out.println("'Toggle Todo Status when Todo Not Found' Test successful");
    }
}