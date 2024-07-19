package com.automaticparking.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;

    protected BaseResponse<T> getResponse(int status, T data) {
        this.status = status;
        this.data = data;
        return this;
    }
}
