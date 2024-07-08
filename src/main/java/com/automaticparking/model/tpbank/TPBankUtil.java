package com.automaticparking.model.tpbank;

import com.automaticparking.model.cash.Cash;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Genarate;
import util.Timestamp;

import java.util.*;

@Service
public class TPBankUtil {
    private Dotenv dotenv;

    @Autowired
    public TPBankUtil(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public int getPointReload() {
        String reloadDefault = dotenv.get("TP_TIMERELOAD");
        int timeReload;
        try {
            timeReload = Integer.parseInt(reloadDefault);
            return 10 / timeReload;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Map<String, String> getDataDate(List<Cash> list) throws Exception {
        Optional<Cash> minCash = list.stream().min(Comparator.comparing(Cash::getCashAt));
        long minCashAt = minCash.get().getCashAt();
        String fromDate = Timestamp.convertTimestampToDate(minCashAt, "yyyyMMdd");
        String toDate = Timestamp.convertTimestampToDate(Genarate.getTimeStamp(), "yyyyMMdd");
        Map<String, String> date = new HashMap<>() {
            {
                put("fromDate", fromDate);
                put("toDate", toDate);
            }
        };
        return date;
    }

    public List<Map<String, Object>> getArrayHistory(String data) {
        try {
            int startIndexList = data.indexOf('[');
            int endIndexList = data.lastIndexOf(']') + 1;
            String jsonArrayString = data.substring(startIndexList, endIndexList);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> list = objectMapper.readValue(jsonArrayString, List.class);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
