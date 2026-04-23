package com.capstone.userservice.dto;

import jakarta.validation.constraints.Pattern;
public class RegisterRequestDTO
{
    @Pattern(regexp = "^[A-Za-z]{2,}$", message = "Name must contain only alphabets and be at least 2 characters")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email must be in format: username@gmail.com")
    private String email;

    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;
    
    private String role;

    //Getter and Setter
    //name
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    
    //email
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    //password
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    
    //phone
    public String getPhone(){return phone;}
    public void setPhone(String phone){this.phone = phone;}
    
    //role
    public String getRole(){return role;}
    public void setRole(String role){this.role = role;}
}