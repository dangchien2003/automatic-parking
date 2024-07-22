package com.automaticparking.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user")
public class Customer {
    @Id
    @Column(length = 30)
    private String uid;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    @JsonIgnore
    private String password;

    @Column
    private Integer block;

    @Column
    private Long lastLogin;

    @Column
    private Long createAt;

    @Column
    private Long acceptAt;

    public Customer(String uid, String email, String password, Long lastLogin, Long createAt, Integer block) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.lastLogin = lastLogin;
        this.createAt = createAt;
        this.block = block;
    }
}
