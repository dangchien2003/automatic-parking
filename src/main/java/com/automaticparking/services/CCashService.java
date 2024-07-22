package com.automaticparking.services;

import com.automaticparking.Repositorys.CashRepository;
import com.automaticparking.Repositorys.CodeRepository;
import com.automaticparking.database.dto.InputMoneyDto;
import com.automaticparking.database.entity.Cash;
import com.automaticparking.database.entity.Code;
import com.automaticparking.database.entity.Customer;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import util.Generate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CCashService {
    private CashRepository cashRepository;
    private CodeRepository codeRepository;
    private CacheService cacheService;

    public ResponseEntity<ResponseSuccess> inputMoney(InputMoneyDto inputMoney, HttpServletRequest request) {

        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");

        String uid = staffDataToken.getUid();
        Cash cash = new Cash();
        cash.setUid(uid);
        cash.setMoney(inputMoney.getMoney());
        cash.setStringCode(inputMoney.getStringCode());
        cash.setCashAt(Generate.getTimeStamp());

        cashRepository.save(cash);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    public ResponseEntity<ResponseSuccess> allMyHistory(HttpServletRequest request) {
        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");

        String uid = customerDataToken.getUid();

        List<Cash> history = cashRepository.findAllByUid(uid);

        // hide field
        for (Cash cash : history) {
            cash.setAcceptBy("hide");
            cash.setRecashBy("hide");
        }
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(history, status), status);
    }

    public ResponseEntity<ResponseSuccess> getMyRemaining(HttpServletRequest request) {
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
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(mapData, status), status);
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

    public Integer getRemaining(String uid) {
        // get history plus cash
        List<Cash> historyCash = cashRepository.findHistoryCashOkByUid(uid);

        Integer totalMyCash = getTotalCash(historyCash);
        // get history code used
        List<Code> myCode = codeRepository.findAllCodeUse(uid, Generate.getTimeStamp());

        Integer moneyUsed = getToTalMoneyUsed(myCode);
        // get remaining(số dư)
        Integer remaining = totalMyCash - moneyUsed;

        return remaining;
    }

    public Integer getToTalMoneyUsed(List<Code> codes) {
        Integer totalMyCash = 0;
        if (codes != null) {
            for (Code code : codes) {
                totalMyCash += (code.getPrice() + code.getPriceExtend());
            }
        }
        return totalMyCash;
    }

}
