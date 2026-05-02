package com.capstone.userservice.dto;

public class LoginResponseDTO
{
    private String token;
    private String email;
    private String role;
    private Long id;


    //Getter and Setter
    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}
    
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public String getRole(){return role;}
    public void setRole(String role){this.role = role;}

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
}