package com.capstone.userservice.dto;

public class RegisterRequestDTO
{
    private String name;
    private String email;
    private String password;
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