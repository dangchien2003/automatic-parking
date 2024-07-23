package com.automaticparking.types;

import com.automaticparking.database.dto.Cookie;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseSuccess {
    private int status;
    private List<Cookie> cookies;
    private Object data;

    public ResponseSuccess(List<Cookie> cookies, Object data, HttpStatus status) {
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
