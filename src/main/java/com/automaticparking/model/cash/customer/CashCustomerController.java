package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cache.CacheService;
import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.customer.dto.InputMoneyDto;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;
import util.Genarate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("customer/cash")
public class CashCustomerController extends ResponseApi {
    private CashCustomerService cashCustomerService = new CashCustomerService();
    private CacheService cacheService;
    @Autowired
    public CashCustomerController(CacheService cacheService) {
        this.cacheService = cacheService;
    }


    @PostMapping("input-money")
    ResponseEntity<?> inputMoney(@Valid @RequestBody InputMoneyDto inputMoney,  HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("customerDataToken");

            String uid = staffDataToken.get("uid");
            Cash cash = new Cash();
            cash.setUid(uid);
            cash.setMoney(inputMoney.money);
            cash.setCashAt(Genarate.getTimeStamp());
            cash.setStringCode(uid);

            Boolean create = cashCustomerService.saveCashHistory(cash);

            if(!create) {
                throw new Exception("Error create history. Please contact for us");
            }

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("all")
    ResponseEntity<?> allMyHistory(HttpServletRequest request) {
        try {
            Map<String, String> customerDataToken = (Map<String, String>) request.getAttribute("customerDataToken");

            String uid = customerDataToken.get("uid");

            List<Cash> history = cashCustomerService.getALlMyHistory(uid);

            // hide field
            for(Cash cash : history) {
                cash.setAcceptBy("hide");
                cash.setRecashBy("hide");
            }

            ResponseSuccess<List<Cash>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = history;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("remaining")
    ResponseEntity<?> getMyRemaining(HttpServletRequest request) {
        try {
            Map<String, String> customerDataToken = (Map <String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            String keyCache = "remaining_" + uid;
            Integer remaining = cacheService.getCache(keyCache);

            if(remaining == null) {
                remaining = cashCustomerService.getRemaining(uid);
                if(!cacheService.setCache(keyCache, remaining)) {
                    System.out.println("Error set cache");
                }
            }

            Map<String, Integer> mapData = new HashMap<>();
            mapData.put("remaining", remaining);
            ResponseSuccess<Map<String, Integer>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = mapData;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
