package com.automaticparking.model.tpbank;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.staff.CashStaffRepository;
import com.automaticparking.types.ResponseSuccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;
import util.Genarate;

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


    @Autowired
    public TPBankService(Dotenv dotenv, TPBankUtil tpBankUtil, CashStaffRepository cashStaffRepository, Executor asyncExecutor) {
        this.dotenv = dotenv;
        this.tpBankUtil = tpBankUtil;
        this.cashStaffRepository = cashStaffRepository;
        this.asyncExecutor = asyncExecutor;
    }

    ResponseEntity<?> autoTpbank(String author) {
        if (!author.equals("admin")) {
            return badRequestApi("error");
        }
        if (runed) {
            return badRequestApi("runed");
        }

        tpBank = tpBank.getInfoAccount();
        if (tpBank == null) {
            return badRequestApi("Error get info account TPBank");
        }

        int pointLoadToken = tpBankUtil.getPointReload();
        if (pointLoadToken <= 0) {
            return badRequestApi("Error get point reload");
        }

        token = login(tpBank).get("access_token").toString();

        try {
            asyncExecutor.execute(() -> {
                try {
                    runed = true;
                    int count = 0;
                    while (true) {
                        List<Cash> cashNotApproves = cashStaffRepository.getAllCashNotApprove();

                        if (cashNotApproves == null) {
                            throw new Exception("Error get cash");
                        }

                        Map<String, String> dataDate = tpBankUtil.getDataDate(cashNotApproves);
                        List<Map<String, Object>> historys = getHistory(tpBank, dataDate);
                        Long[] listIdCashBanked = getCashApprove(cashNotApproves, historys);
                        if (listIdCashBanked.length > 0) {
                            long now = Genarate.getTimeStamp();
                            int updated = cashStaffRepository.approveListCash(listIdCashBanked, now, staff);
                            if (updated != listIdCashBanked.length) {
                                System.out.println(String.format("Error update %d/%d", updated, listIdCashBanked.length));
                            } else {
                                System.out.println(String.format("Success update %d", updated));
                            }
                        }

                        long time = Long.parseLong(dotenv.get("TP_TIMERELOAD")) * 60 * 1000;
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            System.out.println("loi sleep");
                            break;
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

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    private Long[] getCashApprove(List<Cash> listCash, List<Map<String, Object>> historys) throws Exception {
        return listCash.stream()
                .filter(c -> historys.stream()
                        .anyMatch(h -> h.get("description").equals(c.getStringCode()) && Integer.parseInt(h.get("amount").toString()) == c.getMoney()))
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
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status != 200) {
                System.out.println("cannot get token");
                System.out.println("status: " + status);
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(response.body(), Map.class);
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
