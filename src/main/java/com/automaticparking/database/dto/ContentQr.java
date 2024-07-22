package com.automaticparking.database.dto;

import lombok.Data;

@Data
public class ContentQr {
    private String qrid;
    private Integer acceptBot;
    private Long expiredAt;

    public ContentQr(String qrid, Long expiredAt) {
        this.qrid = qrid;
        this.expiredAt = expiredAt;
    }
}
