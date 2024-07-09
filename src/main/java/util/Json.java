package util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Json<T> {
    ObjectMapper objectMapper = new ObjectMapper();

    public String convertToJson(T data) {
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T jsonParse(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }

}
