package com.automaticparking.util;

import com.automaticparking.exception.LogicException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json<T> {
    ObjectMapper objectMapper = new ObjectMapper();

    public String convertToJson(T data) throws
            JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(data);
        return jsonString;
    }

    public T jsonParse(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new LogicException(e.getMessage());
        }
    }

}
