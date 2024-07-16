package com.automaticparking.model.tpbank;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.staff.CashStaffRepository;
import com.automaticparking.types.ResponseSuccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;
import util.Genarate;

import javax.security.sasl.AuthenticationException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.Executor;

@Service
public class TPBankService extends ResponseApi {
    private Executor asyncExecutor;
    private CashStaffRepository cashStaffRepository;
    private Dotenv dotenv;
    private TPBank tpBank = new TPBank();
    private TPBankUtil tpBankUtil;
    private boolean runed = false;
    private String token = "";
    private String staff = "bot";
    private long time;

    private String dvid = "kpVAPvlf34EVbSUJmzPpjURgxxiX1D7CtVbCS8Pt35SQ0";

    @Autowired
    public TPBankService(Dotenv dotenv, TPBankUtil tpBankUtil, CashStaffRepository cashStaffRepository, Executor asyncExecutor) {
        this.dotenv = dotenv;
        this.tpBankUtil = tpBankUtil;
        this.cashStaffRepository = cashStaffRepository;
        this.asyncExecutor = asyncExecutor;
        this.time = Long.parseLong(dotenv.get("TP_TIMERELOAD")) * 60 * 1000;
    }

    ResponseSuccess stopTpbank(String author) throws AuthenticationException, BadRequestException {
        if (!author.equals("admin")) {
            throw new AuthenticationException("Not have access");
        }
        if (!runed) {
            throw new BadRequestException("Not running automatically yet");
        }
        runed = false;
        return new ResponseSuccess();
    }

    ResponseSuccess autoTpbank(String author) throws AuthenticationException, BadRequestException {
        if (!author.equals("admin")) {
            throw new AuthenticationException("Not have access");
        }
        if (runed) {
            throw new BadRequestException("runed");
        }

        tpBank = tpBank.getInfoAccount();
        if (tpBank == null) {
            throw new BadRequestException("Error get info account TPBank");
        }

        int pointLoadToken = tpBankUtil.getPointReload();
        if (pointLoadToken <= 0) {
            throw new BadRequestException("Error get point reload");
        }
        Map<String, Object> dataLogin = login(tpBank);
        if (dataLogin == null) {
            throw new BadRequestException("Error login");
        }

        token = dataLogin.get("access_token").toString();
        runed = true;
        asyncExecutor.execute(() -> {
            try {
                int count = 0;
                List<Cash> cashNotApproves;
                Map<String, String> dataDate;
                List<Map<String, Object>> historys;
                Long[] listIdCashBanked;
                while (runed) {
                    cashNotApproves = cashStaffRepository.getAllCashNotApprove();

                    if (cashNotApproves == null) {
                        throw new Exception("Error get cash");
                    }

                    dataDate = tpBankUtil.getDataDate(cashNotApproves);
                    historys = getHistory(tpBank, dataDate);
                    listIdCashBanked = getCashApprove(cashNotApproves, historys);
                    if (listIdCashBanked.length > 0) {
                        long now = Genarate.getTimeStamp();
                        int updated = cashStaffRepository.approveListCash(listIdCashBanked, now, staff);
                        if (updated != listIdCashBanked.length) {
                            System.out.println(String.format("Error update %d/%d", updated, listIdCashBanked.length));
                        } else {
                            System.out.println(String.format("Success update %d", updated));
                        }
                    }
                    System.out.println("done scan");
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        System.out.println("loi sleep");
                    }
                    ++count;
                    if (count >= pointLoadToken) {
                        token = login(tpBank).get("access_token").toString();
                        count = 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runed = false;
            }
        });

        return new ResponseSuccess();
    }

    private Long[] getCashApprove(List<Cash> listCash, List<Map<String, Object>> historys) throws Exception {
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
                .toArray(Long[]::new);
    }


    private Map<String, Object> login(TPBank tpBank) {
        try {
            String requestBody = "{\"username\":\"" + tpBank.getUsername() + "\",\"password\":\"" + tpBank.getPassword() + "\"}";
            String apiUrl = "https://ebank.tpb.vn/gateway/api/auth/login/v3";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("USER_NAME", "HYD")
                    .header("APP_VERSION", "2024.07.12")
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Language", "vi-VN,vi;q=0.9,fr-FR;q=0.8,fr;q=0.7,en-US;q=0.6,en;q=0.5")
                    .header("Authorization", "Bearer")
                    .header("DEVICE_ID", dvid)
                    .header("DEVICE_NAME", "Chrome")
                    .header("Origin", "https://ebank.tpb.vn")
                    .header("PLATFORM_NAME", "WEB")
                    .header("PLATFORM_VERSION", "126")
                    .header("Referer", "https://ebank.tpb.vn/retail/vX/")
                    .header("SOURCE_APP", "HYDRO")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36")
                    .header("sec-ch-ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"Windows\"")
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
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(jsonDataRes, Map.class);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, Object>> getHistory(TPBank tpBank, Map<String, String> date) {
        try {
            String requestBody = "{\"pageNumber\":1,\"pageSize\":400,\"accountNo\": \"" + tpBank.getAccountNo() + "\",\"currency\":\"VND\",\"fromDate\":\"" + date.get("fromDate") + "\", \"toDate\":\"" + date.get("toDate") + "\",\"keyword\":\"\"  }";
            String apiUrl = "https://ebank.tpb.vn/gateway/api/smart-search-presentation-service/v2/account-transactions/find";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String jsonDataRes = response.body();

            if (status != 200) {
                System.out.println("cannot get history");
                System.out.println("status: " + status);
                System.out.println("data: " + jsonDataRes);
            }
            List<Map<String, Object>> historys = tpBankUtil.getArrayHistory(jsonDataRes);
            return historys;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
