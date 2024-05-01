package com.automaticparking.model.cash.customer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import javax.validation.Valid;

public class InputMoneyDto {
    @Valid

    @NotNull
    @Min(10000)
    public Integer money;
}
