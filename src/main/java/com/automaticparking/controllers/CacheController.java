package com.automaticparking.controllers;

import com.automaticparking.services.CacheService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("cache")
@AllArgsConstructor
public class CacheController  {
    private CacheService cacheService;

    @PostMapping("set")
    ResponseEntity<?> set() {
        return cacheService.set();
    }

    @PostMapping("set1")
    ResponseEntity<?> set1() {
        return cacheService.set1();
    }

    @GetMapping("get")
    ResponseEntity<?> get() {
        return cacheService.get();
    }

    @GetMapping("count")
    ResponseEntity<?> count() {
        return cacheService.count();
    }
}
