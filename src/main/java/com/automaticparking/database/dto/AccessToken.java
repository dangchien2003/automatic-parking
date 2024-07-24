package com.automaticparking.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessToken {
    private String access_token;
    private String token_type;
    private int expires_in;
}
