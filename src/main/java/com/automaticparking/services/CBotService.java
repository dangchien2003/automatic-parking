package com.automaticparking.services;

import com.automaticparking.Repositorys.BotRepository;
import com.automaticparking.database.entity.Bot;
import com.automaticparking.exception.InvalidException;
import com.automaticparking.exception.NotFoundException;
import com.automaticparking.types.ResponseSuccess;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@AllArgsConstructor
public class CBotService {
    private BotRepository botRepository;
    private CacheService cacheService;

    public ResponseEntity<ResponseSuccess> geInfoBot(String id) {
        if (id.isEmpty()) {
            throw new InvalidException("Invalid bot");
        }
        Bot bot = getBot(id);
        if (bot.getCancleAt() != null) {
            throw new NotFoundException();
        }
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(new HashMap<String, String>() {{
            put("id", id);
            put("address", bot.getAddress());
        }}, status), status);
    }

    private Bot getBot(String id) {
        String key = "bot_cache_" + id;
        Bot bot = cacheService.getCache(key);
        if (bot == null) {
            bot = botRepository.findById(id).orElseThrow(() -> new NotFoundException());
            if (bot != null) {
                cacheService.setCache(key, bot);
            }
        }
        return bot;
    }
}
