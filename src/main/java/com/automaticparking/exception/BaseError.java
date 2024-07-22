package com.automaticparking.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import util.Generate;

@Data
public class BaseError {

    private int status;
    private String message;
    private long timestamp;

    protected BaseError setError(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.timestamp = Generate.getTimeStamp();
        return this;
    }
}
