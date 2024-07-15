package com.automaticparking.model.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class LoginGooleDto {
    @Valid

    @NotBlank
    private String googleToken;

    public LoginGooleDto() {
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }
}
