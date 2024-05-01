package com.automaticparking.model.code.customer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qr")
public class Code {
    @Id
    private String qrid;

    @Column
    private String qrCategory;

    @Column
    private String imageIn;

    @Column
    private String imageOut;

    @Column
    private String uid;

    @Column
    private Long buyAt;

    @Column
    private Integer price;

    @Column
    private String checkinAt;

    @Column
    private String checkoutAt;

    @Column
    private Long expireAt;

    @Column
    private String acceptBy;

    @Column
    private String plate;

    @Column
    private String cancleAt;

    public Code() {
    }

    public String getQrid() {
        return qrid;
    }

    public void setQrid(String qrid) {
        this.qrid = qrid;
    }

    public String getQrCategory() {
        return qrCategory;
    }

    public void setQrCategory(String qrCategory) {
        this.qrCategory = qrCategory;
    }

    public String getImageIn() {
        return imageIn;
    }

    public void setImageIn(String imageIn) {
        this.imageIn = imageIn;
    }

    public String getImageOut() {
        return imageOut;
    }

    public void setImageOut(String imageOut) {
        this.imageOut = imageOut;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getBuyAt() {
        return buyAt;
    }

    public void setBuyAt(Long buyAt) {
        this.buyAt = buyAt;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCheckinAt() {
        return checkinAt;
    }

    public void setCheckinAt(String checkinAt) {
        this.checkinAt = checkinAt;
    }

    public String getCheckoutAt() {
        return checkoutAt;
    }

    public void setCheckoutAt(String checkoutAt) {
        this.checkoutAt = checkoutAt;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }

    public String getAcceptBy() {
        return acceptBy;
    }

    public void setAcceptBy(String acceptBy) {
        this.acceptBy = acceptBy;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getCancleAt() {
        return cancleAt;
    }

    public void setCancleAt(String cancleAt) {
        this.cancleAt = cancleAt;
    }
}
