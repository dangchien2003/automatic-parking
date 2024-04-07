package com.automaticparking.types;

import java.util.Map;

public class ResponseError {
    public ResponseError(Map<String, String> message) {
        this.Message = message;
    }
    public final Boolean Success = false;
    public Map<String, String> Message;
}
