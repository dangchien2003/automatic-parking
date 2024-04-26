package encrypt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import util.CustomDotENV;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

public class JWT<T> {

    // Tạo khóa từ chuỗi SECRET_KEY
    private final Key key = new SecretKeySpec("ledangchienledangchienledangchienledangchien".getBytes(), SignatureAlgorithm.HS256.getJcaName());
    private ObjectMapper objectMapper = new ObjectMapper();


    public String createJWT(T data, long second)  {
        try {
            // Thời gian hiện tại
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            // Thời gian hết hạn
            long expMillis = nowMillis + second*1000;
            Date exp = new Date(expMillis);

            String userJson = objectMapper.writeValueAsString(data);
            String jwt = Jwts.builder()
                    .setSubject(userJson)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            return jwt;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Claims decodeJWT(String jwt) {
        try {
            // Giải mã JWT
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);

            // Trả về thông tin trong JWT
            return claimsJws.getBody();
        }catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
