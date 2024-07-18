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
    private long createAt;

    @Column
    private long cancleAt;
}
