package com.capstone.userservice.dto;

public class LoginResponseDTO
{
    private String token;
    private String email;
    private String role;


    //Getter and Setter
    //token
    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}
    
    //email
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    //role
    public String getRole(){return role;}
    public void setRole(String role){this.role = role;}
}