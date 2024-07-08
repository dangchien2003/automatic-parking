package com.automaticparking.model.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    public Customer() {
    }

    public Customer(String uid, String email, String password, Long lastLogin, Long createAt, Integer block) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.lastLogin = lastLogin;
        this.createAt = createAt;
        this.block = block;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getBlock() {
        return block;
    }

    public void setBlock(Integer block) {
        this.block = block;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getAcceptAt() {
        return acceptAt;
    }

    public void setAcceptAt(Long acceptAt) {
        this.acceptAt = acceptAt;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }
}
