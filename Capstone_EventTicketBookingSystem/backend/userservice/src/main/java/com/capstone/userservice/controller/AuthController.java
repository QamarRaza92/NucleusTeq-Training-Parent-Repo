package com.capstone.userservice.controller;

import com.capstone.userservice.dto.LoginRequestDTO;
import com.capstone.userservice.dto.LoginResponseDTO;
import com.capstone.userservice.dto.RegisterRequestDTO;
import com.capstone.userservice.entity.User;
import com.capstone.userservice.repository.UserRepository;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    @Autowired 
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request)
    {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(()-> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().name());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());

        return ResponseEntity.ok(response);
                                                                          
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request)
    {
        if (userRepository.existsByEmail(request.getEmail()))
        {
            Map<String,String> response = new HashMap<>();
            response.put("error", "Email already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if(request.getRole().equalsIgnoreCase("ORGANIZER"))
        {
            user.setRole(User.Role.ORGANIZER);
        }
        else if(request.getRole().equalsIgnoreCase("CUSTOMER"))
        {
            user.setRole(User.Role.CUSTOMER);
        }
        else
        {
            Map<String,String> response = new HashMap<>();
            response.put("error","Invalid role! Use CUSTOMER or ORGANIZER");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        userRepository.save(user);

        Map<String,String> response = new HashMap<>();
        response.put("message","User registered successfully!");
        response.put("email",user.getEmail());
        response.put("role",user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}   