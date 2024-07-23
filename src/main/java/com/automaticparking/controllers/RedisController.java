package com.automaticparking.controllers;

import com.automaticparking.database.dto.Redis;
import com.automaticparking.services.RedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/redis")
@AllArgsConstructor
public class RedisController {
    private RedisService redisService;

    @GetMapping("get")
    public String get(@RequestParam(name = "key", required = true) String key) {
        return redisService.get(key);
    }

    @GetMapping("set")
    public String set(@RequestParam(name = "key", required = true) String key) {
        boolean set = redisService.set(new Redis(key, "1", 10));
        return set ? "ok" : "error";
    }
}
