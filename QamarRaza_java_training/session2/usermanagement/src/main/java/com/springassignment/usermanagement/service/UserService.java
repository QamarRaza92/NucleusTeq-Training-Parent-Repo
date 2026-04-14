package com.springassignment.usermanagement.service;

import org.springframework.stereotype.Service;
import java.util.*;
import com.springassignment.usermanagement.repository.UserRepository;
import com.springassignment.usermanagement.model.User;
import com.springassignment.usermanagement.component.UserNotification;
@Service 
public class UserService
{
    UserService(UserRepository userrepository,UserNotification usernotification)
    {
        this.userrepository = userrepository;
        this.usernotification = usernotification;
    }
    private final UserRepository userrepository;
    private final UserNotification usernotification;

    public User getUserById(int id)
    {
        return userrepository.getUserById(id);
    }

    public List<User> getAllUsers()
    {
        return userrepository.getAllUsers();
    }

    public String addUser(User new_user)
    {
        userrepository.addUser(new_user);

        //Trigger notification
        return usernotification.sendNotification(new_user.getName());
    }

    //To delete a user
    public boolean deleteUser(int id)
    {
        return userrepository.deleteUser(id);
    }

    //To check if a user exists
    public boolean existsById(int id) 
    {
        return userrepository.existsById(id);
    }
}