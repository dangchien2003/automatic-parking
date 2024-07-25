package com.automaticparking.services;

import com.automaticparking.Repositorys.CodeRepository;
import com.automaticparking.Repositorys.QRShopRepository;
import com.automaticparking.database.dto.BuyCodeDto;
import com.automaticparking.database.dto.ContentQr;
import com.automaticparking.database.entity.Code;
import com.automaticparking.database.entity.CodeWithBot;
import com.automaticparking.database.entity.Customer;
import com.automaticparking.database.entity.QrShop;
import com.automaticparking.exception.BadRequestException;
import com.automaticparking.exception.InvalidException;
import com.automaticparking.exception.LogicException;
import com.automaticparking.exception.NotFoundException;
import com.automaticparking.types.ResponseSuccess;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.automaticparking.util.Generate;
import com.automaticparking.util.Json;
import com.automaticparking.util.KeyCache;
import com.automaticparking.util.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


@Service
@AllArgsConstructor
public class CCodeService {

    private QRShopRepository qrShopRepository;
    private CodeRepository codeRepository;
    private CCashService cashCustomerService;
    private CacheService cacheService;
    private Executor asyncExecutor;

    public ResponseEntity<ResponseSuccess> buyCode(BuyCodeDto buyCode, HttpServletRequest request) {

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        // get qr info
        QrShop qr = qrShopRepository.findById(buyCode.getQrCategory()).orElseThrow(() -> new RuntimeException("Not found"));

        // get remaining(số dư)
        Integer remaining = cashCustomerService.getRemaining(uid);

        if (remaining < 0) {
            System.out.println("Số dư nhỏ hơn 0");
        }

        if (remaining < qr.getPrice()) {
            throw new InvalidException("The balance is not enough, please add more money");
        }

        Integer discount = 0;
        // mua code;
        long now = Generate.getTimeStamp();
        Code code = new Code(Generate.generateId("CODE_", 4), qr.getQrCategory(), uid, now, qr.getPrice() - discount, now + qr.getMaxAge() * 1000);

        codeRepository.save(code);

        // reset cache remaining
        asyncExecutor.execute(() -> {
            String keyCache = "remaining_" + uid;
            Integer cacheRemaining = cacheService.getCache(keyCache);
            if (cacheRemaining != null && cacheRemaining > 0) {
                cacheService.setCache(keyCache, cacheRemaining - code.getPrice());
            }
        });
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(code, status), status);
    }

    public ResponseEntity<ResponseSuccess> getBoughtCode(HttpServletRequest request, String quantity) {

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();
        int begin = 0;
        Integer quantityLimit;
        try {
            quantityLimit = Integer.parseInt(quantity.trim());
        } catch (Exception e) {
            quantityLimit = 1000000;
        }

        Pageable pageable = PageRequest.of(begin, quantityLimit);
        List<Code> boughtCode = codeRepository.findByUidOrderByBuyAtDesc(uid, pageable);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(boughtCode, status), status);
    }

    public ResponseEntity<ResponseSuccess> getInfoCode(String qrid, HttpServletRequest request) {
        if (qrid.equals("")) {
            throw new InvalidException("Invalid qr");
        }

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        CodeWithBot code = codeRepository.findCodeWithBot(uid, qrid).orElseThrow(() -> new NotFoundException());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(code, status), status);
    }

    public ResponseEntity<ResponseSuccess> getContenQr(String qrid, HttpServletRequest request) {
        if (qrid == null || qrid.trim().isEmpty()) {
            throw new BadRequestException("Invalid qr");
        }

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        String keyCache = KeyCache.getKeyContentQr(uid, qrid);
        String contentQr = cacheService.getCache(keyCache);

        if (contentQr == null) {
            Code code = codeRepository.findByQridAndUid(qrid, uid).orElseThrow(() -> new NotFoundException("Not found"));

            if (code.getCancleAt() != 0) {
                throw new BadRequestException("Cancled");
            }

            if (code.getCheckoutAt() != 0) {
                throw new BadRequestException("Checkouted");
            }

            // get time expire
            Long exprireAt = code.getExpireAt();

            if (exprireAt <= Generate.getTimeStamp()) {
                throw new BadRequestException("expired");
            }


            ContentQr content = new ContentQr(code.getQrid(), exprireAt);

            if (code.getCheckinAt() == 0) {
                content.setAcceptBot(1);
            } else {
                content.setAcceptBot(2);
            }

            Json<ContentQr> json = new Json<>();
            try {
                contentQr = json.convertToJson(content);
            } catch (JsonProcessingException e) {
                throw new LogicException(e.getMessage());
            }

            if (!cacheService.setCache(keyCache, contentQr)) {
                System.out.println("Error set cache");
            }
        }
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(contentQr, status), status);
    }

    public ResponseEntity<ResponseSuccess> calcPriceExtendCode(String qrid, String date, int indexTime, HttpServletRequest request) {
        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = staffDataToken.getUid();
        Map<String, Object> dataRes = handleCalcPriceExtendCode(qrid, date, indexTime, uid);
        cacheService.setCache("PriceExtendCode" + qrid, dataRes);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(dataRes, status), status);
    }

    private Map<String, Object> handleCalcPriceExtendCode(String qrid, String date, int indexTime, String uid) {
        if (date.trim().isEmpty()) {
            throw new InvalidException("Invalid date");
        }

        long timestamp = Timestamp.convertDateToTimestamp(date, "dd-MM-yyyy");

        if (timestamp <= 0) {
            throw new BadRequestException("Invalid date");
        }

        if (1 > indexTime || indexTime > 4) {
            throw new BadRequestException("Invalid time");
        }

        Code code = codeRepository.findByQridAndUid(qrid, uid).orElseThrow(() -> new NotFoundException("Not found"));
        long now = Generate.getTimeStamp();

        if (code.getCheckinAt() == 0 || code.getCheckoutAt() != 0 || now < code.getExpireAt()) {
            throw new BadRequestException("Code cannot be renewed");
        }

        long newExpire = timestamp + indexTime * 21600000;
        if (now > newExpire - 15 * 60 * 1000) {
            throw new BadRequestException("The time must be at least 15 minutes greater than the current time");
        }

        long hoursExpired = (newExpire - code.getExpireAt()) / 1000 / 3600;

        int price = (int) hoursExpired * 1000;

        Map dataRes = new HashMap<String, Object>() {
            {
                put("date", date);
                put("indexTime", indexTime);
                put("price", price);
                put("dateTime", newExpire);
                put("code", code);
            }
        };

        return dataRes;
    }

    public ResponseEntity<ResponseSuccess> extendCode(String qrid, String date, int indexTime, HttpServletRequest request) {
        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = staffDataToken.getUid();

        Map<String, Object> data = cacheService.getCache("PriceExtendCode" + qrid);
        if (data == null || !data.get("date").equals(date) || !data.get("indexTime").equals(indexTime)) {
            data = handleCalcPriceExtendCode(qrid, date, indexTime, uid);
        }

        int price = (int) data.get("price");
        int remaining = getRemainingUser(uid);
        if (remaining < price) {
            throw new BadRequestException("Not enough money");
        }

        long newExpire = (long) data.get("dateTime");

        Code code = (Code) data.get("code");
        code.setExpireAt(newExpire);
        code.setPriceExtend(code.getPriceExtend() + price);
        code.setTimesExtend(code.getTimesExtend() + 1);

        codeRepository.save(code);

        cacheService.setCache("remaining_" + uid, remaining - price);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    private int getRemainingUser(String idUser) {

        Integer remaining = cacheService.getCache("remaining_" + idUser);
        if (remaining != null) {
            return remaining;
        }

        return cashCustomerService.getRemaining(idUser);
    }

    public ResponseEntity<ResponseSuccess> cancleCode(String qrid, HttpServletRequest request) {

        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = staffDataToken.getUid();

        qrid = qrid.trim();
        if (qrid.equals("")) {
            throw new BadRequestException("Invalid code");
        }

        Code code = codeRepository.findByQridAndUid(qrid, uid).orElseThrow(() -> new NotFoundException("Not Found"));
        if (code == null) {
            throw new BadRequestException("Code not exist");
        }
        if (code.getCheckinAt() != 0 || code.getCancleAt() != 0) {
            throw new BadRequestException("Cannot cancle");
        }

        long now = Generate.getTimeStamp();
        code.setCancleAt(now);

        codeRepository.save(code);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }
}
