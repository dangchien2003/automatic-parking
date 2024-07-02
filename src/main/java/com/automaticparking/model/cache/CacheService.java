package com.automaticparking.model.cache;

import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseSuccess;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

@Service
public class CacheService extends ResponseApi {
    private Cache<String, Object> cache;

    @Autowired
    public CacheService(Cache<String, Object> cache) {
        this.cache = cache;
    }

    ResponseEntity<?> set() {
        try {
            Staff staff = new Staff();
            staff.setAdmin(5);
            Boolean set = setCache("staff", staff);
            if (!set) {
                throw new Exception("Lỗi set cache");
            }
            ResponseSuccess<Staff> response = new ResponseSuccess<>();
            response.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> set1() {
        try {
            Staff staff = new Staff();
            staff.setAdmin(2);
            Boolean set = setCache("staff", staff);
            if (!set) {
                throw new Exception("Lỗi set cache");
            }
            ResponseSuccess<Staff> response = new ResponseSuccess<>();
            response.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> get() {
        try {
            Staff staff = getCache("staff");
            ResponseSuccess<Staff> response = new ResponseSuccess<>();
            response.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> count() {
        try {
            Long count = countCache();
            ResponseSuccess<Long> response = new ResponseSuccess<>();
            response.data = count;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
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
