package util;

import io.github.cdimascio.dotenv.Dotenv;

public class CustomDotENV {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}
