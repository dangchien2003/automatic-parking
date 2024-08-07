package com.automaticparking.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Timestamp {
    public static long convertDateToTimestamp(String time, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

            if (format.contains("HH")) {
                LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
                return dateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toEpochSecond();
            } else {
                LocalDateTime dateTime = LocalDateTime.parse(time + " 00:00:00", DateTimeFormatter.ofPattern(format + " HH:mm:ss"));
                return dateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant().toEpochMilli();
            }
        } catch (DateTimeParseException e) {
            System.out.println("Định dạng không hỗ trợ: " + e.getMessage());
            return -1;
        }
    }

    public static String convertTimestampToDate(long timestamp, String format) {
        Instant time = Instant.ofEpochMilli(timestamp);
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedDateTime = time.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }
}
