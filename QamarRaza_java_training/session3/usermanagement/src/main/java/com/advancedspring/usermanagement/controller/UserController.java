package com.advancedspring.usermanagement.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.advancedspring.usermanagement.model.User;

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

    @GetMapping("/search")
    public List<User> getUsers(
                            @RequestParam(required=false) Integer id,
                            @RequestParam(required=false) String name,
                            @RequestParam(required=false) int age,
                            @RequestParam(required=false) String gender,
                            @RequestParam(required=false) String email
                             )
    {
        return userservice.getUsers();
    }




}