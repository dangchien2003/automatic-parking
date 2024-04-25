package util;

import java.security.SecureRandom;

public class Random {
    // Phương thức để tạo chuỗi ngẫu nhiên
    public static String generateRandomString(int length) {
        // Ký tự được phép xuất hiện trong chuỗi ngẫu nhiên
        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        // Tạo chuỗi ngẫu nhiên bằng cách chọn một ký tự ngẫu nhiên từ danh sách các ký tự được phép
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allowedChars.length());
            char randomChar = allowedChars.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
