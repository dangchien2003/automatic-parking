package com.automaticparking.model.bot;

import com.automaticparking.model.cache.CacheService;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class BotUtil {
    private BotRepository botRepository;
    private CacheService cacheService;

    public BotUtil(BotRepository botRepository, CacheService cacheService) {
        this.botRepository = botRepository;
        this.cacheService = cacheService;
    }

    public Bot getBot(String id) throws SQLException {
        String key = genKeyCache(id);
        Bot bot = cacheService.getCache(key);
        if (bot == null) {
            bot = botRepository.getInfo(id);
            if (bot != null) {
                cacheService.setCache(key, bot);
            }
        }
        return bot;
    }

    public String genKeyCache(String id) {
        return "bot_cache_" + id;
    }
}
