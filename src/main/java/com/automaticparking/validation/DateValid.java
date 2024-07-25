package com.automaticparking.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

public class DateValid {
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            .toFormatter();

    public boolean isValidDate(String dateStr) {
        try {
            LocalDate parsedDate = LocalDate.parse(dateStr, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
