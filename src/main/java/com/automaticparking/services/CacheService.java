package com.automaticparking.services;

import com.automaticparking.database.entity.Staff;
import com.automaticparking.types.ResponseSuccess;
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

    public ResponseSuccess set() throws Exception {
        Staff staff = new Staff();
        staff.setAdmin(5);
        Boolean set = setCache("staff", staff);
        if (!set) {
            throw new Exception("Lỗi set cache");
        }
        return new ResponseSuccess(staff);
    }

    public ResponseSuccess set1() throws Exception {
        Staff staff = new Staff();
        staff.setAdmin(2);
        Boolean set = setCache("staff", staff);
        if (!set) {
            throw new Exception("Lỗi set cache");
        }
        return new ResponseSuccess(staff);
    }

    public ResponseSuccess get() throws Exception {
        return new ResponseSuccess(getCache("staff"));
    }

    public ResponseSuccess count() throws Exception {
        return new ResponseSuccess(countCache());
    }


    public <T> T getCache(String key) {
        T data;
        try {
            Object value = cache.getIfPresent(key);
            if (value == null) {
                return null;
            }
            data = (T) value;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return data;
    }

    public <T> Boolean setCache(String key, T value) {
        try {
            cache.put(key, value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public Long countCache() {
        return cache.estimatedSize();
    }

}
