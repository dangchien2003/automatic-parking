package com.automaticparking.database.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import javax.validation.Valid;

@Data
public class BuyCodeDto {
    @Valid

    @NotBlank
    @Size(max = 10)
    private String qrCategory;
}
