package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;

public class Json<T> {
    ObjectMapper objectMapper = new ObjectMapper();

    public String convertToJson(T data) throws
            JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(data);
        return jsonString;
    }

    public T jsonParse(String json, Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(json, type);
    }

}
