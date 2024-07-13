package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cache.CacheService;
import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.customer.dto.InputMoneyDto;
import com.automaticparking.model.code.customer.Code;
import com.automaticparking.model.code.customer.CodeRepository;
import com.automaticparking.model.customer.Customer;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;
import util.Genarate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CashCustomerService {
    private CashCustomerRepository cashCustomerRepository;
    private CodeRepository codeRepository;
    private CacheService cacheService;

    @Autowired
    public CashCustomerService(CashCustomerRepository cashCustomerRepository, CodeRepository codeRepository, CacheService cacheService) {
        this.cashCustomerRepository = cashCustomerRepository;
        this.codeRepository = codeRepository;
        this.cacheService = cacheService;
    }

    ResponseSuccess inputMoney(InputMoneyDto inputMoney, HttpServletRequest request) throws Exception {

        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");

        String uid = staffDataToken.getUid();
        Cash cash = new Cash();
        cash.setUid(uid);
        cash.setMoney(inputMoney.getMoney());
        cash.setStringCode(inputMoney.getStringCode());
        cash.setCashAt(Genarate.getTimeStamp());

        Boolean create = cashCustomerRepository.saveCashHistory(cash);

        if (!create) {
            throw new Exception("Error create history. Please contact for us");
        }
        return new ResponseSuccess();
    }

    ResponseSuccess allMyHistory(HttpServletRequest request) throws SQLException, Exception {
        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");

        String uid = customerDataToken.getUid();

        List<Cash> history = cashCustomerRepository.getALlMyHistory(uid);

        // hide field
        for (Cash cash : history) {
            cash.setAcceptBy("hide");
            cash.setRecashBy("hide");
        }
        return new ResponseSuccess(history);
    }

    ResponseSuccess getMyRemaining(HttpServletRequest request) throws SQLException {
        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        String keyCache = "remaining_" + uid;
        Integer remaining = cacheService.getCache(keyCache);

        if (remaining == null) {
            remaining = getRemaining(uid);
            if (!cacheService.setCache(keyCache, remaining)) {
                System.out.println("Error set cache");
            }
        }

        Map<String, Integer> mapData = new HashMap<>();
        mapData.put("remaining", remaining);
        return new ResponseSuccess(mapData);
    }

    public Integer getTotalCash(List<Cash> historyCash) {
        Integer totalMyCash = 0;
        if (historyCash != null) {
            for (Cash cash : historyCash) {
                totalMyCash += cash.getMoney();
            }
        }
        return totalMyCash;
    }

    public Integer getRemaining(String uid) throws SQLException {
        // get history plus cash
        List<Cash> historyCash = cashCustomerRepository.getALlMyHistoryOk(uid);

        Integer totalMyCash = getTotalCash(historyCash);
        // get history code used
        List<Code> myCode = codeRepository.getAllCodeUse(uid);

        Integer moneyUsed = codeRepository.getToTalMoneyUsed(myCode);
        // get remaining(số dư)
        Integer remaining = totalMyCash - moneyUsed;

        return remaining;
    }
}
