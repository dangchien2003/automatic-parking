package encrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import util.DotENV;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

public class JWT<T> {
    private final Key key = new SecretKeySpec(DotENV.get("KEY_JWT").getBytes(), SignatureAlgorithm.HS256.getJcaName());
    private ObjectMapper objectMapper = new ObjectMapper();

    public String createJWT(T data, long second) {
        try {
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            long expMillis = nowMillis + second * 1000;
            Date exp = new Date(expMillis);

            String userJson = objectMapper.writeValueAsString(data);
            String jwt = Jwts.builder()
                    .setSubject(userJson)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            return jwt;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Claims decodeJWT(String jwt) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);

            return claimsJws.getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
