package com.automaticparking.database.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import javax.validation.Valid;

public class ForgetPassword {
    @Valid

    @Email
    @NotBlank
    public String email;

    public Long lastLogin;
}
