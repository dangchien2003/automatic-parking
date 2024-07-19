package com.automaticparking.controllers;

import com.automaticparking.services.CacheService;
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
            return ResponseEntity.ok(cacheService.set());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("set1")
    ResponseEntity<?> set1() {
        try {
            return ResponseEntity.ok(cacheService.set1());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("get")
    ResponseEntity<?> get() {
        try {
            return ResponseEntity.ok(cacheService.get());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("count")
    ResponseEntity<?> count() {
        try {
            return ResponseEntity.ok(cacheService.count());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
