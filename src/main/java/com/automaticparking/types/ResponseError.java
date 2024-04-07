package com.automaticparking.types;

import java.util.Map;

public class ResponseError {
    public ResponseError(Map<String, String> message) {
        this.message = message;
    }
    public final Boolean success = false;
    public Map<String, String> message;
}
