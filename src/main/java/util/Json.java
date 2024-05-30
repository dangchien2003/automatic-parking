package util;
import com.fasterxml.jackson.databind.ObjectMapper;
public class Json<T> {
    public String convertToJson(T data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(data);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
