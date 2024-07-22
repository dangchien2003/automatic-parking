package com.automaticparking.services;

import com.automaticparking.database.entity.Staff;
import com.automaticparking.exception.LogicException;
import com.automaticparking.types.ResponseSuccess;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CacheService {
    private Cache<String, Object> cache;

    public ResponseEntity<ResponseSuccess> set() {
        Staff staff = new Staff();
        staff.setAdmin(5);
        Boolean set = setCache("staff", staff);
        if (!set) {
            throw new LogicException("Lỗi set cache");
        }
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(staff, status), status);
    }

    public ResponseEntity<ResponseSuccess> set1() {
        Staff staff = new Staff();
        staff.setAdmin(2);
        Boolean set = setCache("staff", staff);
        if (!set) {
            throw new LogicException("Lỗi set cache");
        }

        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(staff, status), status);
    }

    public ResponseEntity<ResponseSuccess> get() {
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(getCache("staff"), status), status);
    }

    public ResponseEntity<ResponseSuccess> count() {
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(countCache(), status), status);
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
