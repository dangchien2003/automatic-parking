package com.automaticparking.services;

import com.automaticparking.database.dto.Redis;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@AllArgsConstructor
public class RedisService {
    private Jedis jedis;

    public boolean set(Redis redis) {
        try {
            String set = jedis.setex(redis.getKey(), redis.getAge(), redis.getValue());
            if (!set.equals("OK")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String get(String key) {
        try {
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
