package com.capstone.eventservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.capstone.eventservice.dto.UserResponseDTO;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserClient {
    @GetMapping("/api/auth/user/{userId}")
    UserResponseDTO getUserById(@PathVariable("userId") Long id);
}