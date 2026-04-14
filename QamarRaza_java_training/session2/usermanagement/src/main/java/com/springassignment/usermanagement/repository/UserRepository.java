package com.springassignment.usermanagement.repository;

import org.springframework.stereotype.Repository;
import java.util.*;
import com.springassignment.usermanagement.model.User;


@Repository
public class UserRepository
{
    private List<User> users = new ArrayList<>(
                                                 Arrays.asList
                                                 (
                                                    new User(1,"Qamar Raza",21,"raza@gamil.com"),
                                                    new User(2,"Aale Mohammad",21,"aale@gamil.com"),
                                                    new User(3,"Aadil Maqbool",23,"aadil@gamil.com"),
                                                    new User(4,"Affan Khan",22,"khan@gamil.com"),
                                                    new User(5,"Aaftab Sheikh",22,"sheikh@gamil.com")
                                                 )
                                                );

    public User getUserById(int id)
    {
        return users.stream().filter(u -> u.getId()==id).findFirst().orElse(null);
    }

    public List<User> getAllUsers()
    {
        return users;
    }

    public void addUser(User new_user)
    {
        users.add(new_user);
    }

    //To delete a user
    public boolean deleteUser(int id)
    {
        return users.removeIf(u -> u.getId()==id);
    }

    //To check if a user exists
    public boolean existsById(int id) 
    {
        return users.stream().anyMatch(u -> u.getId() == id);
    }
}