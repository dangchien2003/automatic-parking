package com.automaticparking.database.dto;

import jakarta.validation.constraints.Email;

import javax.validation.Valid;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStaffDto {
    @Valid

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String birthday;

    @NotBlank
    private String name;
}
