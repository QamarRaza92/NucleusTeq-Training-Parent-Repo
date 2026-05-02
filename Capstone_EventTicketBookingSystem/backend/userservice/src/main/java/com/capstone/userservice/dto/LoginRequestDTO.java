package com.capstone.userservice.dto;
import jakarta.validation.constraints.*;
public class LoginRequestDTO
{
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email must be in format: username@gmail.com")
    @NotBlank
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$",
            message = "Password must be 8-12 chars with 1 uppercase, 1 number, 1 special char")
    @NotBlank
    private String password;

    //Getter and Setter
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    //password
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
}