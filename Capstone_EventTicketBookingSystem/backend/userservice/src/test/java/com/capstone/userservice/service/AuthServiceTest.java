package com.capstone.userservice.service;

import com.capstone.userservice.dto.RegisterRequestDTO;
import com.capstone.userservice.entity.User;
import com.capstone.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Password@123");
        request.setPhone("9876543210");
        request.setRole("CUSTOMER");
    }

    @Test
    void testEmailExists_ReturnsTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        boolean exists = authService.emailExists("test@example.com");
        
        assertTrue(exists);
    }

    @Test
    void testEmailExists_ReturnsFalse() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        
        boolean exists = authService.emailExists("new@example.com");
        
        assertFalse(exists);
    }

    @Test
    void testRegisterUser_Success() {
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded_hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User savedUser = authService.registerUser(request);

        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encoded_hash", savedUser.getPassword());
        assertEquals(User.Role.CUSTOMER, savedUser.getRole());
        
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_OrganizerRole() {
        request.setRole("ORGANIZER");
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded_hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User savedUser = authService.registerUser(request);

        assertEquals(User.Role.ORGANIZER, savedUser.getRole());
    }

    @Test
    void testRegisterUser_InvalidRole_ThrowsException() {
        request.setRole("INVALID_ROLE");

        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(request);
        });
    }
}