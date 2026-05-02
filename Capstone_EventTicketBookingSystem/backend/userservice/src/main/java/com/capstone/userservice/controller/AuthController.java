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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request)
    {
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
        return ResponseEntity.ok(response);
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("Invalid email or password", HttpStatus.UNAUTHORIZED.value()));
        }                                               
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request)
    {
        if (authService.emailExists(request.getEmail()))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO("Email already exists!",HttpStatus.CONFLICT.value()));
        }
        try
        {
            User user = authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponseDTO("User registered successfully!", user.getEmail(), user.getRole().name()));
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponseDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(response);
    }
}   