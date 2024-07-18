package com.automaticparking.model.code.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

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
    private long buyAt;

    @Column
    private Integer price;

    @Column
    private long checkinAt;

    @Column
    private long checkoutAt;

    @Column
    private long expireAt;

    @Column
    private String acceptBy;

    @Column
    private String plate;

    @Column
    private long cancleAt;

    @Column
    private int timesExtend;

    @Column
    private int priceExtend;

    @Column
    @JoinColumn(table = "bot", name = "id")
    private String botId;

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

    public Long getCheckinAt() {
        return checkinAt;
    }

    public void setCheckinAt(Long checkinAt) {
        this.checkinAt = checkinAt;
    }

    public Long getCheckoutAt() {
        return checkoutAt;
    }

    public void setCheckoutAt(Long checkoutAt) {
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

    public long getCancleAt() {
        return cancleAt;
    }

    public void setCancleAt(long cancleAt) {
        this.cancleAt = cancleAt;
    }

    public int getTimesExtend() {
        return timesExtend;
    }

    public void setTimesExtend(int timesExtend) {
        this.timesExtend = timesExtend;
    }

    public int getPriceExtend() {
        return priceExtend;
    }

    public void setPriceExtend(int priceExtend) {
        this.priceExtend = priceExtend;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }
}
