package com.automaticparking.services;

import com.automaticparking.database.entity.Bot;
import com.automaticparking.repositorys.BotRepository;
import com.automaticparking.types.ResponseSuccess;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;

@Service
public class CBotService {
    private BotRepository botRepository;
    private CacheService cacheService;

    @Autowired
    public CBotService(BotRepository botRepository, CacheService cacheService) {
        this.botRepository = botRepository;
        this.cacheService = cacheService;

    }

    public ResponseSuccess geInfoBot(String id) throws SQLException, NotFoundException {
        Bot bot = getBot(id);
        if (bot == null || bot.getCancleAt() != null) {
            throw new NotFoundException("Not found bot");
        }

        return new ResponseSuccess(new HashMap<String, String>() {{
            put("id", id);
            put("address", bot.getAddress());
        }});
    }

    private Bot getBot(String id) throws SQLException {
        String key = "bot_cache_" + id;
        Bot bot = cacheService.getCache(key);
        if (bot == null) {
            bot = botRepository.getInfo(id);
            if (bot != null) {
                cacheService.setCache(key, bot);
            }
        }
        return bot;
    }
}
