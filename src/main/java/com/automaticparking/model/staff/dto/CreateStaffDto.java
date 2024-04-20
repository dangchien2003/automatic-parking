package com.automaticparking.model.staff.dto;

import jakarta.validation.constraints.Email;

import javax.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStaffDto {
    @Valid

    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String birthday;

    @NotBlank
    public String name;

}
