package com.automaticparking.model.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailDto {
    @Valid

    @Email
    @NotBlank
    public String newEmail;
}
