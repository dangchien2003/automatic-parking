package com.automaticparking.model.cash.staff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ApproveDto {
    @Valid

    @NotEmpty
    public Long[] listIdCash;
}
