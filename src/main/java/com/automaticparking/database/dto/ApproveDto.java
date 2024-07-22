package com.automaticparking.database.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ApproveDto {
    @Valid

    @NotEmpty
    private Long[] listIdCash;
}
