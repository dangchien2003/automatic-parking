package com.automaticparking.services;

import com.automaticparking.Repositorys.CashRepository;
import com.automaticparking.database.dto.TPBank;
import com.automaticparking.database.entity.Cash;
import com.automaticparking.exception.AuthorizedException;
import com.automaticparking.exception.LogicException;
import com.automaticparking.types.ResponseSuccess;
import com.automaticparking.util.Generate;
import com.automaticparking.util.Timestamp;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TPBankService {
    private Dotenv dotenv;
    private CashRepository cashRepository;
    private TPBank tpBank = new TPBank().getInfoAccount();
    private String token = "";
    private String staff = "bot";
    private final AtomicReference<Instant> lastGetToken = new AtomicReference<>();
    private final String dvid = "kpVAPvlf34EVbSUJmzPpjURgxxiX1D7CtVbCS8Pt35SQ0";

    @Autowired
    public TPBankService(Dotenv dotenv, CashRepository cashRepository) {
        this.dotenv = dotenv;
        this.cashRepository = cashRepository;
    }

    public ResponseEntity<ResponseSuccess> autoTpbank(String author) {
        if (!author.equals("admin")) {
            throw new AuthorizedException("Not have access");
        }
        return autoTpbank();
    }

    public ResponseEntity<ResponseSuccess> autoTpbank() {
        System.out.println("start scan");
        if (tpBank == null) {
            throw new LogicException("Error get info account TPBank");
        }

        Instant lastTime = lastGetToken.get();
        if (lastTime == null || ChronoUnit.MILLIS.between(lastTime, Instant.now()) >= Long.parseLong(dotenv.get("TP_TIME_REFRESH")) * 60 * 1000) {
            Map<String, Object> dataLogin = login(tpBank);
            token = dataLogin.get("access_token").toString();
            lastGetToken.set(Instant.now());
        }

        List<Cash> cashNotApproves;
        Map<String, String> dataDate;
        List<Map<String, Object>> historys;
        List<Long> listIdCashBanked;

        cashNotApproves = cashRepository.findAllCashNotApprove();

        dataDate = getDataDate(cashNotApproves);
        historys = getHistory(tpBank, dataDate);
        listIdCashBanked = getCashApprove(cashNotApproves, historys);
        if (!listIdCashBanked.isEmpty()) {
            long now = Generate.getTimeStamp();
            int updated = cashRepository.approveListCash(now, staff, listIdCashBanked);
            if (updated != listIdCashBanked.size()) {
                System.out.println(String.format("Error update %d/%d", updated, listIdCashBanked.size()));
            } else {
                System.out.println(String.format("Success update %d", updated));
            }
        }
        System.out.println("done scan");

        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    private List<Long> getCashApprove(List<Cash> listCash, List<Map<String, Object>> historys) {
        return listCash.stream()
                .filter(c -> historys.stream()
                        .anyMatch(h -> {
                            String description = h.get("description").toString();
//                            int length = description.length();
//                            if (length >= 15) {
//                                description = description.substring(0, 15);
//                            }
//                            return description.equals(c.getStringCode()) && Integer.parseInt(h.get("amount").toString()) == c.getMoney();
                            return description.contains(c.getStringCode()) && Integer.parseInt(h.get("amount").toString()) == c.getMoney();
                        }))
                .map(Cash::getStt)
                .toList();
    }


    private Map<String, Object> login(TPBank tpBank) {
        try {
            String requestBody = "{\"username\":\"" + tpBank.getUsername() + "\",\"password\":\"" + tpBank.getPassword() + "\"}";
            String apiUrl = "https://ebank.tpb.vn/gateway/api/auth/login/v3";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Content-Type", "application/json")
//                    .header("USER_NAME", "HYD")
//                    .header("APP_VERSION", "2024.07.12")
//                    .header("Accept", "application/json, text/plain, */*")
//                    .header("Accept-Language", "vi-VN,vi;q=0.9,fr-FR;q=0.8,fr;q=0.7,en-US;q=0.6,en;q=0.5")
//                    .header("Authorization", "Bearer")
                    .header("DEVICE_ID", dvid)
//                    .header("DEVICE_NAME", "Chrome")
//                    .header("Origin", "https://ebank.tpb.vn")
                    .header("PLATFORM_NAME", "WEB")
//                    .header("PLATFORM_VERSION", "126")
//                    .header("Referer", "https://ebank.tpb.vn/retail/vX/")
//                    .header("SOURCE_APP", "HYDRO")
//                    .header("Sec-Fetch-Dest", "empty")
//                    .header("Sec-Fetch-Mode", "cors")
//                    .header("Sec-Fetch-Site", "same-origin")
//                    .header("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36")
//                    .header("sec-ch-ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
//                    .header("sec-ch-ua-mobile", "?0")
//                    .header("sec-ch-ua-platform", "\"Windows\"")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonDataRes = response.body();
            int status = response.statusCode();
            if (status != 200) {
                System.out.println("cannot get token");
                System.out.println("status: " + status);
                System.out.println("data: " + jsonDataRes);
                tpBank.print();
                throw new LogicException("Can not login");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(jsonDataRes, Map.class);
            return map;
        } catch (Exception e) {
            throw new LogicException(e.getMessage());
        }
    }

    private List<Map<String, Object>> getHistory(TPBank tpBank, Map<String, String> date) {
        String requestBody = "{\"pageNumber\":1,\"pageSize\":400,\"accountNo\": \"" + tpBank.getAccountNo() + "\",\"currency\":\"VND\",\"fromDate\":\"" + date.get("fromDate") + "\", \"toDate\":\"" + date.get("toDate") + "\",\"keyword\":\"\"  }";
        String apiUrl = "https://ebank.tpb.vn/gateway/api/smart-search-presentation-service/v2/account-transactions/find";

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new LogicException(e.getMessage());
        }


        int status = response.statusCode();
        String jsonDataRes = response.body();

        if (status != 200) {
            System.out.println("cannot get history");
            System.out.println("status: " + status);
            System.out.println("data: " + jsonDataRes);
        }
        List<Map<String, Object>> historys = getArrayHistory(jsonDataRes);
        return historys;
    }

    public Map<String, String> getDataDate(List<Cash> list) {
        Optional<Cash> minCash = list.stream().min(Comparator.comparing(Cash::getCashAt));
        long minCashAt = minCash.get().getCashAt();
        String fromDate = Timestamp.convertTimestampToDate(minCashAt, "yyyyMMdd");
        String toDate = Timestamp.convertTimestampToDate(Generate.getTimeStamp(), "yyyyMMdd");
        Map<String, String> date = new HashMap<>() {
            {
                put("fromDate", fromDate);
                put("toDate", toDate);
            }
        };
        return date;
    }

    public List<Map<String, Object>> getArrayHistory(String data) {
        int startIndexList = data.indexOf('[');
        int endIndexList = data.lastIndexOf(']') + 1;
        String jsonArrayString = data.substring(startIndexList, endIndexList);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> list;
        try {
            list = objectMapper.readValue(jsonArrayString, List.class);
        } catch (Exception e) {
            throw new LogicException(e.getMessage());
        }
        return list;
    }
}
