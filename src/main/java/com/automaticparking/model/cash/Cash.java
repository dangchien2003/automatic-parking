package com.automaticparking.model.cash;

import javax.persistence.*;

@Entity
@Table(name = "historycash")
public class Cash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stt;

    public Cash() {
    }

    @Column
    private String uid;

    @Column
    private Integer money;

    @Column(length = 30)
    private String stringCode;

    @Column
    private Long cashAt;

    @Column
    private Long cancleAt;

    @Column
    private String recashBy;

    @Column
    private Long recashAt;

    @Column
    private Long acceptAt;

    @Column
    private String acceptBy;

    public Long getRecashAt() {return recashAt; }

    public void setRecashAt(Long recashAt) { this.recashAt = recashAt; }

    public Long getStt() {
        return stt;
    }

    public void setStt(Long stt) {
        this.stt = stt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getStringCode() {
        return stringCode;
    }

    public void setStringCode(String stringCode) {
        this.stringCode = stringCode;
    }

    public Long getCashAt() {
        return cashAt;
    }

    public void setCashAt(Long cashAt) {
        this.cashAt = cashAt;
    }

    public Long getCancleAt() {
        return cancleAt;
    }

    public void setCancleAt(Long cancleAt) {
        this.cancleAt = cancleAt;
    }

    public String getRecashBy() {
        return recashBy;
    }

    public void setRecashBy(String recashBy) {
        this.recashBy = recashBy;
    }

    public Long getAcceptAt() {
        return acceptAt;
    }

    public void setAcceptAt(Long acceptAt) {
        this.acceptAt = acceptAt;
    }

    public String getAcceptBy() {
        return acceptBy;
    }

    public void setAcceptBy(String acceptBy) {
        this.acceptBy = acceptBy;
    }

}
