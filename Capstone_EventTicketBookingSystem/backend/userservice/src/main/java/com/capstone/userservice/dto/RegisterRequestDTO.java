package com.capstone.userservice.dto;

import jakarta.validation.constraints.Pattern;
public class RegisterRequestDTO
{
    @NotBlank
    @Pattern(regexp = "^[A-Za-z ]{2,}$", message = "Name must contain only alphabets and spaces, and be at least 2 characters")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email must be in format: username@gmail.com")
    @NotBlank
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$",
            message = "Password must be 8-12 chars with 1 uppercase, 1 number, 1 special char")
    @NotBlank
    private String password;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;
    
    @NotBlank
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