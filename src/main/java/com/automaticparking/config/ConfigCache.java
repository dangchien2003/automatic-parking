package com.automaticparking.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.DotENV;

import java.util.concurrent.TimeUnit;

@Configuration
public class ConfigCache {
    @Bean
    public Cache<String, Object> setupCache() {
        long lifeCache = 0;
        try {
            lifeCache = Long.parseLong(DotENV.get("EXPIRE_CACHE_SECOND"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Cache<String, Object> cache = Caffeine.newBuilder()
//                    .maximumSize(Integer.parseInt(DotENV.get("MAX_CACHE"))) // Số lượng mục tối đa trong cache
                    .expireAfterWrite(lifeCache, TimeUnit.SECONDS) // Thời gian sống của mỗi mục trong cache tính từ lúc ghi
//                    .expireAfterAccess(Long.parseLong(DotENV.get("EXPIRE_CACHE_SECOND")), TimeUnit.SECONDS) // Thời gian sống của mỗi mục trong cache tính từ lần cuối truy cập cache
                    .build();
            System.out.println("Config cache ok");
            return cache;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
