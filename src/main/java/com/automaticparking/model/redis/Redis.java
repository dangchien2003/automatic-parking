package com.automaticparking.model.redis;

public class Redis {
    private String key;
    private String value;
    private int age;

    public Redis(String key, String value, int age) {
        this.key = key;
        this.value = value;
        this.age = age;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
