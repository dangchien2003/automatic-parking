package com.automaticparking.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@NoArgsConstructor
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

    public Code(String qrid, String qrCategory, String uid, long buyAt, Integer price, long expireAt) {
        this.qrid = qrid;
        this.qrCategory = qrCategory;
        this.uid = uid;
        this.buyAt = buyAt;
        this.price = price;
        this.expireAt = expireAt;
    }
}
