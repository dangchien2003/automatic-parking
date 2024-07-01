package com.automaticparking.model.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptChangeEmailDto {
    @Valid

    @NotBlank
    public String tokenChange;
}
