package com.automaticparking.model.staff.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import javax.validation.*;

@Data
public class LoginDto {
    @Valid

    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String password;
}