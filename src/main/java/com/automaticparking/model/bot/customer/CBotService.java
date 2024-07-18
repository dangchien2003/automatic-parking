package com.automaticparking.model.bot.customer;

import com.automaticparking.model.bot.Bot;
import com.automaticparking.model.bot.BotRepository;
import com.automaticparking.model.bot.BotUtil;
import com.automaticparking.types.ResponseSuccess;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;

@Service
public class CBotService {
    private BotRepository botRepository;

    private BotUtil botUtil;

    @Autowired
    public CBotService(BotRepository botRepository, BotUtil botUtil) {
        this.botRepository = botRepository;
        this.botUtil = botUtil;
    }

    public ResponseSuccess getBot(String id) throws SQLException, NotFoundException {
        Bot bot = botUtil.getBot(id);
        if (bot == null || bot.getCancleAt() != null) {
            throw new NotFoundException("Not found bot");
        }

        return new ResponseSuccess(new HashMap<String, String>() {{
            put("id", id);
            put("address", bot.getAddress());
        }});
    }

}
