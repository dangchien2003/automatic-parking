package com.automaticparking.model.bot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bot")
public class Bot {
    @Id
    private String id;

    @Column
    private String address;

    @Column
    private Long createAt;

    @Column
    private Long cancleAt;

    public Bot() {
    }

    public Bot(String id, String address, long createAt) {
        this.id = id;
        this.address = address;
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getCancleAt() {
        return cancleAt;
    }

    public void setCancleAt(Long cancleAt) {
        this.cancleAt = cancleAt;
    }
}
