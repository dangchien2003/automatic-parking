package com.automaticparking.model.code.customer;

public class ContentQr {
    private String qrid;
    private Integer acceptBot;
    private Long expiredAt;

    public ContentQr(String qrid, Long expiredAt) {
        this.qrid = qrid;
        this.expiredAt = expiredAt;
    }

    public String getQrid() {
        return qrid;
    }

    public void setQrid(String qrid) {
        this.qrid = qrid;
    }

    public Integer getAcceptBot() {
        return acceptBot;
    }

    public void setAcceptBot(Integer acceptBot) {
        this.acceptBot = acceptBot;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }
}
