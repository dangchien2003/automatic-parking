package com.automaticparking.types;

import java.util.Map;

public class ResponseSuccess {
    private final Boolean success = true;
    private Map<String, String> cookies;
    private Object data;

    public ResponseSuccess(Map<String, String> cookies, Object data) {
        this.cookies = cookies;
        this.data = data;
    }

    public ResponseSuccess(Object data) {
        this.data = data;
    }

    public ResponseSuccess() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
