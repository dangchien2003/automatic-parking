package com.automaticparking.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;
import util.DotENV;

@Configuration
public class ConfigRedis {
    @Bean
    public Jedis redis() {
        Jedis jedis = new Jedis();
        try {
            jedis = new Jedis(DotENV.get("REDIS_HOST"), Integer.parseInt(DotENV.get("REDIS_PORT")));
            jedis.auth(DotENV.get("REDIS_AUTH"));
            System.out.println("Redis OK");
        } catch (Exception e) {
            System.out.println("Error redis");
            e.printStackTrace();
        }
        return jedis;
    }
}
