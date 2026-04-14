package com.springassignment.usermanagement.controller;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.example.demo.service.UserService;
import com.example.demo.model.User;
@RestController 
@RequestMapping("/users")
public class UserController
{
    private final UserService userservice;
    UserController(UserService userservice)
    {
        this.userservice = userservice;
    }

    //1. Get all users
    @GetMapping
    public List<User> getAllUsers()
    {
        return userservice.getAllUsers();
    }

    //2. Get users by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id)
    {
        return userservice.getUserById(id);
    }

    //3. Add new user
    @PostMapping
    public String addUser(@RequestBody User new_user)
    {
        if(userservice.existsById(new_user.getId()))
        {
            return "User already exists !!";
        }

        return userservice.addUser(new_user);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id)
    {
        if(userservice.deleteUser(id))
        {
            return "User data deleted successfully.";
        }
        else
        {
            return "User does not exist !!";
        }
    }
}