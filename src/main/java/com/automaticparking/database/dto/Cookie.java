package com.automaticparking.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cookie {
    private String name;
    private String value;
    private int age;
}
