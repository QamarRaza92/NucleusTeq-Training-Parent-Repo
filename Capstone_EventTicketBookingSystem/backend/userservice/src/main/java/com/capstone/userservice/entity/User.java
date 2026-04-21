package com.capstone.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity 
@Table(name='users')
public class User
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY);
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false,unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role
    {
        CUSTOMER,ORGANIZER
    }

    
    //Getter & Setter methods 
    //id
    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    //name
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    
    //email
    public String getEmail(){return email;}
    public void setEmail(String id){this.email = email;}

    //password
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    
    //phone
    public String getPhone(){return phone;}
    public void setPhone(String phone){this.phone = phone;}
    
    //role
    public Long getId(){return role;}
    public void setRole(Role role){this.role = role;}
}