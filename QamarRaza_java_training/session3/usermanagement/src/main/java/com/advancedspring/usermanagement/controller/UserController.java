package com.advancedspring.usermanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;
import com.advancedspring.usermanagement.model.User;
import com.advancedspring.usermanagement.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController
{
    private final UserService userservice;
    //Constructor injection
    public UserController(UserService userservice)
    {
        this.userservice = userservice;
    }

    @GetMapping
    public List<User> getAllUsers()
    {
        return userservice.getAllUsers();
    }

    @GetMapping("/search")
    public List<User> getUsers(
                            @RequestParam(required=false) Integer id,
                            @RequestParam(required=false) String name,
                            @RequestParam(required=false) Integer age,
                            @RequestParam(required=false) String gender,
                            @RequestParam(required=false) String email
                             )
    {
        return userservice.getUsers(id,name,age,gender,email);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id, @RequestParam(required=false) Boolean confirm)
    {
        return userservice.deleteUser(id,confirm);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitUser(@RequestBody User user)
    {
        return userservice.submitUser(user);
    }
}