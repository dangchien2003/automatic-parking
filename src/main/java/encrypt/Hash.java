package encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public Boolean compareHash(String value, String Hash) throws NoSuchAlgorithmException {
        String hashValue = this.hash(value);

        if (hashValue.equals(Hash)) {
            return true;
        } else {
            return false;
        }
    }

    public String hash(String input) throws NoSuchAlgorithmException {
        String algorithm = "SHA-256";
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(input.getBytes());

        // Chuyển mảng byte thành dạng hex string
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
