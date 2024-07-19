package com.automaticparking.database.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import javax.validation.Valid;

public class RegisterDto {
    @Valid

    @NotBlank
    @Email
    public String email;

    @NotBlank
    @Size(min = 8, max = 30)
    public String password;
}
