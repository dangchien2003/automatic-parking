package com.automaticparking.model.staff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import javax.validation.Valid;

public class UpdateStaffDto {
    @Valid

    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String birthday;

    @NotBlank
    public String name;
}
