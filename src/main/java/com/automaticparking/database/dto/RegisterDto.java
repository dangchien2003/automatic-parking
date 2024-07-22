package com.automaticparking.database.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterDto extends LoginDto {
    @Override
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    public String getPassword() {
        return super.getPassword();
    }
}