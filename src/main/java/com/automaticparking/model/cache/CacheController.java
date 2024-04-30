package com.automaticparking.model.cache;

import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

@RestController
@RequestMapping("cache")
public class CacheController extends ResponseApi {
    private CacheService cacheService;
    @Autowired
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("set")
    ResponseEntity<?> set() {
        try {
            Staff staff = new Staff();
            staff.setAdmin(5);
            Boolean set = cacheService.setCache("staff", staff);
            if(!set) {
                throw new Exception("Lỗi set cache");
            }
            ResponseSuccess<Staff> response = new ResponseSuccess<>();
            response.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("set1")
    ResponseEntity<?> set1() {
        try {
            Staff staff = new Staff();
            staff.setAdmin(2);
            Boolean set = cacheService.setCache("staff", staff);
            if(!set) {
                throw new Exception("Lỗi set cache");
            }
            ResponseSuccess<Staff> response = new ResponseSuccess<>();
            response.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("get")
    ResponseEntity<?> get() {
        try {
            Staff staff = cacheService.getCache("staff");
            ResponseSuccess<Staff> response = new ResponseSuccess<>();
            response.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("count")
    ResponseEntity<?> count() {
        try {
            Long count = cacheService.countCache();
            ResponseSuccess<Long> response = new ResponseSuccess<>();
            response.data = count;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

}
