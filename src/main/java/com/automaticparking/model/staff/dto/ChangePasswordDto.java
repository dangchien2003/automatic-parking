package com.automaticparking.model.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import javax.validation.Valid;

@Data
public class ChangePasswordDto {
    @Valid

    @NotBlank
    public String oldPassword;

    @NotBlank
    @Size(min = 8)
    public String newPassword;

    @NotBlank
    @Size(min = 8)
    public String confirmPassword;
}
