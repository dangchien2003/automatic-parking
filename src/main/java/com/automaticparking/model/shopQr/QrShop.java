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

    @Column Long maxAge;

    public QrShop() {
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
