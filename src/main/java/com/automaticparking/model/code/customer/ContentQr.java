package com.automaticparking.model.code.customer;

public class ContentQr {
    private String qrid;
    private Integer acceptBot;
    private Long renderAt;

    public ContentQr(String qrid, Long time) {
        this.qrid = qrid;
        this.renderAt = time;
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

    public Long getRenderAt() {
        return renderAt;
    }

    public void setRenderAt(Long renderAt) {
        this.renderAt = renderAt;
    }
}
