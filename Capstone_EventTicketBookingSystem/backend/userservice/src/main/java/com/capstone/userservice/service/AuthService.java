package com.capstone.userservice.service;

import com.capstone.userservice.dto.RegisterRequestDTO;
import com.capstone.userservice.entity.User;
import com.capstone.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(RegisterRequestDTO request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole().equalsIgnoreCase("ORGANIZER")) {
            user.setRole(User.Role.ORGANIZER);
        } else if (request.getRole().equalsIgnoreCase("CUSTOMER")) {
            user.setRole(User.Role.CUSTOMER);
        } else {
            throw new IllegalArgumentException("Invalid role! Use CUSTOMER or ORGANIZER");
        }

        return userRepository.save(user);
    }
}