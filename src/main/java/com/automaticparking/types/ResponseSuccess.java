package com.automaticparking.types;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class ResponseSuccess {
    private int status;
    private Map<String, String> cookies;
    private Object data;

    public ResponseSuccess(Map<String, String> cookies, Object data, HttpStatus status) {
        this.cookies = cookies;
        this.data = data;
        this.status = status.value();
    }

    public ResponseSuccess(Object data, HttpStatus status) {
        this.data = data;
        this.status = status.value();
    }

    public ResponseSuccess(HttpStatus status) {
        this.status = status.value();
    }
}
