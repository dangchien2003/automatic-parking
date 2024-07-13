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
public class CacheService {
    private Cache<String, Object> cache;

    @Autowired
    public CacheService(Cache<String, Object> cache) {
        this.cache = cache;
    }

    ResponseSuccess set() throws Exception {
        Staff staff = new Staff();
        staff.setAdmin(5);
        Boolean set = setCache("staff", staff);
        if (!set) {
            throw new Exception("Lỗi set cache");
        }
        return new ResponseSuccess(staff);
    }

    ResponseSuccess set1() throws Exception {
        Staff staff = new Staff();
        staff.setAdmin(2);
        Boolean set = setCache("staff", staff);
        if (!set) {
            throw new Exception("Lỗi set cache");
        }
        return new ResponseSuccess(staff);
    }

    ResponseSuccess get() throws Exception {
        return new ResponseSuccess(getCache("staff"));
    }

    ResponseSuccess count() throws Exception {
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
