package com.automaticparking.model.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisService {
    private Jedis jedis;

    @Autowired
    public RedisService(Jedis jedis) {
        this.jedis = jedis;
    }

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
