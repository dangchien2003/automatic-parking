package com.automaticparking.types;

public class ResponseException extends RuntimeException{
    private int status;
    public ResponseException(String message, int status){
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
