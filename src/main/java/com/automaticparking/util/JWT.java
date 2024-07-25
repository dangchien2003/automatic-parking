package com.automaticparking.util;

import com.automaticparking.exception.LogicException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@AllArgsConstructor
@Component
public class JWT {
    private Dotenv dotenv;
    private ObjectMapper objectMapper;

    private Key getSigningKey() {
        String secretKey = dotenv.get("KEY_JWT"); // Lấy khóa bí mật từ biến môi trường
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public String createJWT(Object data, long second) {
        try {
            long nowMillis = Generate.getTimeStamp();
            Date now = new Date(nowMillis);

            long expMillis = nowMillis + second * 1000;
            Date exp = new Date(expMillis);

            String userJson = objectMapper.writeValueAsString(data);
            String jwt = Jwts.builder()
                    .setSubject(userJson)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
            return jwt;
        } catch (JsonProcessingException e) {
            throw new LogicException(e.getMessage());
        }
    }

    public Claims decodeJWT(String jwt) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt);

            return claimsJws.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
