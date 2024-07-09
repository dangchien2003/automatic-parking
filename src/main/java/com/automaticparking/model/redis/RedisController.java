package com.automaticparking.model.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("redis")
public class RedisController {
    private RedisService redisService;

    @Autowired
    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

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
