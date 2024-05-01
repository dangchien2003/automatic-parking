package com.automaticparking.model.code.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import javax.validation.Valid;

@Data
public class BuyCodeDto {
    @Valid

    @NotBlank
    @Size(max=10)
    public String qrCategory;
}
