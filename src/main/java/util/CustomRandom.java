package util;

import java.util.Random;

public class CustomRandom {
    public static String randomLetters(Integer length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            // Sinh ra một số nguyên ngẫu nhiên từ 97 đến 122 (tương ứng với 'a' đến 'z' trong bảng mã ASCII)
            int asciiValue = random.nextInt(26) + 97;
            char randomChar = (char) asciiValue;
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
