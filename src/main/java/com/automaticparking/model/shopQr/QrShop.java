package com.automaticparking.model.shopQr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shopqr")
public class QrShop {
    @Id
    private String qrCategory;

    @Column
    private Integer price;

    @Column
    private Long maxAge;

    @Column
    private Integer hide;

    public QrShop() {
    }

    public QrShop(String qrCategory, Integer price, Long maxAge, Integer hide) {
        this.qrCategory = qrCategory;
        this.price = price;
        this.maxAge = maxAge;
        this.hide = hide;
    }

    public Integer getHide() {
        return hide;
    }

    public void setHide(Integer hide) {
        this.hide = hide;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public String getQrCategory() {
        return qrCategory;
    }

    public void setQrCategory(String qrCategory) {
        this.qrCategory = qrCategory;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
