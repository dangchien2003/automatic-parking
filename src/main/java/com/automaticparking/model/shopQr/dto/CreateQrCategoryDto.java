package com.automaticparking.model.shopQr.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import javax.validation.Valid;

@Data
public class CreateQrCategoryDto {
    @Valid

    @NotBlank
    @Size(min=3, max = 10)
    public String qrCategory;

    @NotNull
    @Min(0)
    public Integer price;

    @NotNull
    @Min(60)
    public Long maxAge;
}
