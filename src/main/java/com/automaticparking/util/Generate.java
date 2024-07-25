package com.automaticparking.util;

import com.automaticparking.exception.LogicException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Random;

public class Generate {
    public static Long getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        long time = zdt.toInstant().toEpochMilli();
        return time;
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
            throw new LogicException(e.getMessage());
        }

        for (Map.Entry<String, String> entry : data.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }
        return data;
    }

    public static String randomLettersUpper(Integer length) {
        java.util.Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int asciiValue = random.nextInt(26) + 97;
            char randomChar = (char) asciiValue;
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String generateId(String prefix, int lenRandom) {
        return prefix + Generate.getTimeStamp() + "_" + randomLettersUpper(lenRandom);
    }

}
