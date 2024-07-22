package com.automaticparking.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "bot")
public class Bot {
    @Id
    private String id;

    @Column
    private String address;

    @JsonIgnore
    @Column
    private Long createAt;
    
    @JsonIgnore
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
