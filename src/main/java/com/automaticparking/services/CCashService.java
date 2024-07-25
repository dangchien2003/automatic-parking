package com.automaticparking.services;

import com.automaticparking.Repositorys.CashRepository;
import com.automaticparking.Repositorys.CodeRepository;
import com.automaticparking.database.dto.InputMoneyDto;
import com.automaticparking.database.entity.Cash;
import com.automaticparking.database.entity.Code;
import com.automaticparking.database.entity.Customer;
import com.automaticparking.exception.LogicException;
import com.automaticparking.types.ResponseSuccess;
import com.automaticparking.util.Generate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Service

public class CCashService {
    @Autowired
    private CashRepository cashRepository;
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private Executor asyncExecutor;
    @Autowired
    private TPBankService tpBankService;
    private boolean await = false;

    public ResponseEntity<ResponseSuccess> inputMoney(InputMoneyDto inputMoney, HttpServletRequest request) {

        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");

        String uid = staffDataToken.getUid();
        Cash cash = new Cash();
        cash.setUid(uid);
        cash.setMoney(inputMoney.getMoney());
        cash.setStringCode(inputMoney.getStringCode());
        cash.setCashAt(Generate.getTimeStamp());

        asyncExecutor.execute(() -> {
            if (await == true) {
                return;
            }
            await = true;
            try {
                long timeSleep = 60 * 1000;
                Thread.sleep(timeSleep);
                tpBankService.autoTpbank();
            } catch (Exception e) {
                System.out.println("Lỗi scan: " + e.getMessage());
                throw new LogicException(e.getMessage());
            }
            await = false;
        });

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
