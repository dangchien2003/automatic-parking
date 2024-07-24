package com.automaticparking.database.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}