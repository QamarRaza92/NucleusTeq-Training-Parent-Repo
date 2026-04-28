package com.capstone.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Pattern;

@Entity 
@Table(name="users")
public class User
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
   @Pattern(regexp = "^[A-Za-z ]{2,}$", message = "Name must contain only alphabets and spaces, and be at least 2 characters")
    private String name;

    @Column(nullable=false,unique=true)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email must be in format: username@gmail.com")
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
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
    public void setEmail(String email){this.email = email;}

    //password
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    
    //phone
    public String getPhone(){return phone;}
    public void setPhone(String phone){this.phone = phone;}
    
    //role
    public Role getRole(){return role;}
    public void setRole(Role role){this.role = role;}
}