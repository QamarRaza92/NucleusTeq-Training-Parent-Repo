package com.advancedspring.usermanagement.repository;

import com.advancedspring.usermanagement.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Repository
public class UserRepository
{
    //All users list
    private List<User> users= new ArrayList<>(
                                        Arrays.asList
                                        (
                                            new User(1,"Qamar Raza",21,"male","raza@gmail.com"),
                                            new User(2,"Asha Bhosle",94,"female","bhosle@gmail.com"),
                                            new User(3,"Sheldon Cooper",37,"male","cooper@gmail.com"),
                                            new User(4,"Leonard Hoftadter",38,"male","hofstadter@gmail.com"),
                                            new User(5,"Penny",35,"female","penny@gmail.com"),
                                            new User(6,"Michael Scott",45,"male","scott@gmail.com"),
                                            new User(7,"Pamela Beesly",32,"female","beesly@gmail.com"),
                                            new User(8,"Jim Halpert",34,"male","halpert@gmail.com"),
                                            new User(9,"Walter White",50,"male","white@gmail.com"),
                                            new User(10,"Jesse Pinkman",30,"male","pinkman@gmail.com")
                                        )
                                        );

    //getAllUsers 
    public List<User> getAllUsers()
    {
        return users;
    }

    //deleteById, To delete a user
    public boolean deleteById(Integer id)
    {
        return users.removeIf(u -> u.getId().equals(id));
    }

    //To check if a user exists
    public boolean userExistsById(Integer id) 
    {
        return users.stream().anyMatch(u -> u.getId().equals(id));
    }

    //Add new user
    public void addUser(User new_user)
    {
        users.add(new_user);
    }
}