package com.automaticparking.database.dto;

import io.github.cdimascio.dotenv.Dotenv;


public class TPBank {
    private Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();
    ;

    private String username;
    private String password;
    private String accountNo;
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TPBank getInfoAccount() {
        String username = dotenv.get("TP_USERNAME");
        String password = dotenv.get("TP_PASSWORD");
        String accountNo = dotenv.get("TP_ACCOUNTNO");
        if (username == null || password == null || accountNo == null) {
            System.out.println("Info TPBank not exist");
            return null;
        }

        this.username = username;
        this.password = password;
        this.accountNo = accountNo;
        return this;
    }

    public void print() {
        System.out.println("username: " + this.username);
        System.out.println("password: " + this.password);
        System.out.println("accountNo: " + this.accountNo);
        System.out.println("token: " + this.token);
    }
}
