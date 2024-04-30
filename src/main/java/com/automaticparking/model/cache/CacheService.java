package com.automaticparking.model.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private Cache<String, Object> cache;

    @Autowired
    public CacheService(Cache<String, Object> cache) {
        this.cache = cache;
    }

    public <T> T getCache(String key) {
        T data;
        try {
            Object value = cache.getIfPresent(key);
            if(value == null) {
                return null;
            }
            data = (T) value;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return data;
    }

    public <T> Boolean setCache(String key, T value) {
        try {
            cache.put(key, value);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public Long countCache() {
        return cache.estimatedSize();
    }

}
