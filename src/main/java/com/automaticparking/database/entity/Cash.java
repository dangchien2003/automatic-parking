package com.automaticparking.database.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
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
}
