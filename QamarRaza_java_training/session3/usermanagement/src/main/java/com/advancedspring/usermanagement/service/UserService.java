package com.advancedspring.usermanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import com.advancedspring.usermanagement.repository.UserRepository;
import com.advancedspring.usermanagement.model.User;

@Service
public class UserService
{
    private final UserRepository userrepository;
    //Constructor injection
    public UserService(UserRepository userrepository)
    {
        this.userrepository = userrepository;
    }

    //Getting all users
    public List<User> getAllUsers()
    {
        return userrepository.getAllUsers();
    }

    //GetUser: Filter users to show to client
    public List<User> getUsers(Integer id, String name, Integer age, String gender, String email)
    {
        List<User> users = userrepository.getAllUsers();
        return users.stream().filter( user ->
                                    (id == null || user.getId().equals(id)) &&
                                    (name == null || user.getName().equalsIgnoreCase(name)) &&
                                    (age == null || user.getAge().equals(age)) &&
                                    (gender == null || user.getGender().equalsIgnoreCase(gender)) &&
                                    (email == null || user.getEmail().equalsIgnoreCase(email)) 
                                    ).toList();
    }

    //Delete: write logic to delete user
    public ResponseEntity<String> deleteUser(Integer id,Boolean confirm)
    {
        if(confirm==null || !confirm)
        {
            return ResponseEntity.status(400).body("Confirmation Required!! pass confirm=true as query parameter");
        }
        else
        {
            boolean deleted = userrepository.deleteById(id);

            if(deleted)
            {
                return ResponseEntity.ok("User with id "+id+" has been deleted successfully.");
            }
            else
            {
                return ResponseEntity.status(404).body("User with id "+id+" not found !!");
            }
        }
    }

    //Submit: Create a new user
    public ResponseEntity<String> submitUser(User user)
    {
        if(user.getId()==null || 
                user.getName()==null || user.getName().isEmpty() ||
                user.getAge()==null || 
                user.getGender()==null || 
                user.getEmail()==null || user.getEmail().isEmpty()
                )
        {
            return ResponseEntity.status(400).body("Invalid Input");
        }

        if(userrepository.userExistsById(user.getId()))
        {
            return ResponseEntity.status(409).body("Error: User already exists.");
        }
        
        else
        {
            userrepository.addUser(user);
            return ResponseEntity.status(201).body("User '"+user.getName()+"' with id:"+user.getId()+" inserted successfully :)");
        }
    }

}