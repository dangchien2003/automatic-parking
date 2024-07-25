package com.automaticparking.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
@AllArgsConstructor
public class ConfigRedis {
    private Dotenv dotenv;

    @Bean
    public Jedis redis() {
        Jedis jedis = new Jedis();
        try {
            jedis = new Jedis(dotenv.get("REDIS_HOST"), Integer.parseInt(dotenv.get("REDIS_PORT")));
            jedis.auth(dotenv.get("REDIS_AUTH"));
            System.out.println("Redis OK");
        } catch (Exception e) {
            System.out.println("Error redis");
            e.printStackTrace();
        }
        return jedis;
    }
}
