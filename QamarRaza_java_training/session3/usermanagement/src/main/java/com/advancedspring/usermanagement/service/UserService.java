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

    public List<User> getAllUsers()
    {
        return userrepository.getAllUsers();
    }

    public List<User> getUsers(Integer id, String name, Integer age, String gender, String email)
    {
        List<User> users = userrepository.getAllUsers();
        return users.stream().filter( user ->
                                    (id == null || user.getId().equals(id)) &&
                                    (name == null || user.getName().equals(name)) &&
                                    (age == null || user.getAge().equals(age)) &&
                                    (gender == null || user.getGender().equals(gender)) &&
                                    (email == null || user.getEmail().equals(email)) 
                                    ).toList();
    }

    public ResponseEntity<String> deleteUser(Integer id,Boolean confirm)
    {
        if(confirm==null || !confirm)
        {
            return ResponseEntity.status(400).body("Confirmation Required!! pass confirm=true as query parameter");
        }
        else
        {
            boolean deleted = userrepository.deleteById(id,confirm);

            if(delete)
            {
                return ResponseEntity.ok("User with id "+id+" has been deleted successfully.");
            }
            else
            {
                return ResponseEntity.status(404).body("User with id "+id+" not found !!");
            }
        }
    }

    public ResponseEntity<String> submitUser(User user)
    {
    }

}