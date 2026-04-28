package com.capstone.userservice.dto;

public class LoginRequestDTO
{
    private String email;
    private String password;


    //Getter and Setter
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    //password
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
}