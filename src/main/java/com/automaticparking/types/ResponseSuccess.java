package com.automaticparking.types;

import java.util.Map;

public class ResponseSuccess<T> {
    public final Boolean success = true;
    public Map<String, String> cookies = null;
    public T data = null;
}
