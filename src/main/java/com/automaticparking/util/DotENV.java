package com.automaticparking.util;

import io.github.cdimascio.dotenv.Dotenv;

public class DotENV {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}
