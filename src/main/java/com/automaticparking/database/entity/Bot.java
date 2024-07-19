package com.automaticparking.database.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
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
}
