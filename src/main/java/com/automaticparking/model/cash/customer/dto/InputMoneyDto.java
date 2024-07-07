package com.automaticparking.model.cash.customer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.validation.Valid;

public class InputMoneyDto {
    @Valid

    @NotNull
    @Min(10000)
    private Integer money;
    @NotBlank
    private String stringCode;

    public InputMoneyDto() {
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getStringCode() {
        return stringCode;
    }

    public void setStringCode(String stringCode) {
        this.stringCode = stringCode;
    }
}
