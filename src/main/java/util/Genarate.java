package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class Genarate {
    public static Long getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return zdt.toInstant().toEpochMilli();
    }

    public static Map<String, String> getMapFromJson(String json) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> data = null;
        try {
            // Chuyển chuỗi JSON thành Map
            data = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            data = null;
        }

        for (Map.Entry<String, String> entry : data.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }
        return data;
    }
}
