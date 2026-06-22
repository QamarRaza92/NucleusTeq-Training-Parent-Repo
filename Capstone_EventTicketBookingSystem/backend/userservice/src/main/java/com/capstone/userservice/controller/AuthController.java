package com.capstone.userservice.controller;

import com.capstone.userservice.dto.LoginRequestDTO;
import com.capstone.userservice.dto.LoginResponseDTO;
import com.capstone.userservice.dto.RegisterRequestDTO;
import com.capstone.userservice.dto.UserResponseDTO;
import com.capstone.userservice.dto.SuccessResponseDTO;
import com.capstone.userservice.dto.ErrorResponseDTO;
import com.capstone.userservice.entity.User;
import com.capstone.userservice.repository.UserRepository;
import com.capstone.userservice.service.AuthService; 
import com.capstone.userservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    @Autowired
    private AuthService authService;

    @Autowired 
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    // @Autowired
    // PasswordEncoder passwordEncoder; (Removed this logic from controller to service)

    @Autowired
    AuthenticationManager authenticationManager;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request)
    {
        log.info("Login Attempt - Email:{}",request.getEmail());
        try
        {
            Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(()-> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail(),user.getRole().name(),user.getId());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().name());
            response.setId(user.getId());
            log.info("Login Successful: User Id:{}, Role:{}",user.getId(),user.getRole().name());
            return ResponseEntity.ok(response);
        }
        catch(Exception e)
        {
            log.warn("Login failed - Invalid credentials for email: {}",request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("Invalid email or password", HttpStatus.UNAUTHORIZED.value()));
        }                                               
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request)
    {
        log.info("New Registration Attempt - Email:{}",request.getEmail());
        if (authService.emailExists(request.getEmail()))
        {
            log.warn("Registration Failed: Email:{} already exists",request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO("Email already exists!",HttpStatus.CONFLICT.value()));
        }
        try
        {
            User user = authService.registerUser(request);
            log.info("Registration Successful: User:{} registered as '{}'",request.getEmail(),request.getRole());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponseDTO("User registered successfully!", user.getEmail(), user.getRole().name()));
        }
        catch(IllegalArgumentException e)
        {
            log.warn("Registration Failed: Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> 
                                {
                                  log.error("Failed to fetch user details");
                                  return new RuntimeException("User not found");                  
                                }

                            );
        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
        log.info("Feign Client: Successfully fetched details of User:{}",response.getEmail());
        return ResponseEntity.ok(response);
    }
}   

//Can implement global exception handler to avoid code repetition.