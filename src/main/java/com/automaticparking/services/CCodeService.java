package com.automaticparking.services;

import com.automaticparking.database.dto.ContentQr;
import com.automaticparking.database.entity.Code;
import com.automaticparking.repositorys.CCodeRepository;
import com.automaticparking.database.dto.BuyCodeDto;
import com.automaticparking.database.entity.Customer;
import com.automaticparking.database.entity.QrShop;
import com.automaticparking.repositorys.QrShopRepository;
import com.automaticparking.types.ResponseSuccess;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Generate;
import util.Json;
import util.KeyCache;
import util.Timestamp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


@Service
public class CCodeService {

    private QrShopRepository qrShopService;
    private CCodeRepository codeRepository;
    private CCashService cashCustomerService;
    private CacheService cacheService;
    private Executor asyncExecutor;

    @Autowired
    public CCodeService(Executor asyncExecutor, CacheService cacheService, CCashService cashCustomerService, CCodeRepository codeRepository, QrShopRepository qrShopService) {
        this.asyncExecutor = asyncExecutor;
        this.cacheService = cacheService;
        this.cashCustomerService = cashCustomerService;
        this.codeRepository = codeRepository;
        this.qrShopService = qrShopService;
    }

    public ResponseSuccess buyCode(BuyCodeDto buyCode, HttpServletRequest request) throws SQLException, BadRequestException {

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        // get qr info
        QrShop qr = qrShopService.getOneQrById(buyCode.qrCategory);

        if (qr == null) {
            throw new BadRequestException("Qr not exist");
        }

        // get remaining(số dư)
        Integer remaining = cashCustomerService.getRemaining(uid);

        if (remaining < 0) {
            System.out.println("Số dư nhỏ hơn 0");
        }

        if (remaining < qr.getPrice()) {
            throw new BadRequestException("The balance is not enough, please add more money");
        }

        Integer discount = 0;
        // mua code;
        long now = Generate.getTimeStamp();
        Code code = new Code(Generate.generateId("CODE_", 4), qr.getQrCategory(), uid, now, qr.getPrice() - discount, now + qr.getMaxAge() * 1000);

        codeRepository.saveCode(code);

        // reset cache remaining
        asyncExecutor.execute(() -> {
            String keyCache = "remaining_" + uid;
            Integer cacheRemaining = cacheService.getCache(keyCache);
            if (cacheRemaining != null && cacheRemaining > 0) {
                cacheService.setCache(keyCache, cacheRemaining - code.getPrice());
            }
        });
        return new ResponseSuccess(code);

    }

    public ResponseSuccess getBoughtCode(HttpServletRequest request, String quantity) throws SQLException {

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        Integer quantityLimit;
        try {
            quantityLimit = Integer.parseInt(quantity.trim());
        } catch (Exception e) {
            quantityLimit = 1000000;
        }

        List<Code> boughtCode = codeRepository.allBoughtCode(uid, quantityLimit);
        return new ResponseSuccess(boughtCode);
    }


    public ResponseSuccess getInfoCode(String qrid, HttpServletRequest request) throws BadRequestException, NotFoundException, SQLException {
        if (qrid == null) {
            throw new BadRequestException("Invalid qr");
        }

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        Code code = codeRepository.getInfo(uid, qrid);

        if (code == null) {
            throw new NotFoundException("Not found");
        }

        if (code.getAcceptBy() != null) {
            code.setAcceptBy("1");
        }
        return new ResponseSuccess(code);
    }

    public ResponseSuccess getContenQr(String qrid, HttpServletRequest request) throws BadRequestException, NotFoundException, SQLException, JsonProcessingException {
        if (qrid == null || qrid.trim().isEmpty()) {
            throw new BadRequestException("Invalid qr");
        }

        Customer customerDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerDataToken.getUid();

        String keyCache = KeyCache.getKeyContentQr(uid, qrid);
        String contentQr = cacheService.getCache(keyCache);

        if (contentQr == null) {
            Code code = codeRepository.getInfo(uid, qrid);

            if (code == null) {
                throw new NotFoundException("Not found");
            }

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
            contentQr = json.convertToJson(content);

            if (!cacheService.setCache(keyCache, contentQr)) {
                System.out.println("Error set cache");
            }
        }

        return new ResponseSuccess(contentQr);
    }

    public ResponseSuccess calcPriceExtendCode(String qrid, String date, int indexTime, HttpServletRequest request) throws BadRequestException, NotFoundException, SQLException {
        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = staffDataToken.getUid();

        if (date.trim().isEmpty()) {
            throw new BadRequestException("Invalid date");
        }

        long timestamp = Timestamp.convertDateToTimestamp(date, "dd-MM-yyyy");

        if (timestamp <= 0) {
            throw new BadRequestException("Invalid date");
        }

        if (1 > indexTime || indexTime > 4) {
            throw new BadRequestException("Invalid time");
        }

        Code code = codeRepository.getInfo(uid, qrid);
        if (code == null) {
            throw new NotFoundException("Code not found");
        }

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

        cacheService.setCache("PriceExtendCode" + qrid, dataRes);

        return new ResponseSuccess(dataRes);
    }

    public ResponseSuccess extendCode(String qrid, String date, int indexTime, HttpServletRequest request) throws BadRequestException, NotFoundException, SQLException {
        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = staffDataToken.getUid();

        Map<String, Object> data = cacheService.getCache("PriceExtendCode" + qrid);
        if (data == null || !data.get("date").equals(date) || !data.get("indexTime").equals(indexTime)) {
            ResponseSuccess res = calcPriceExtendCode(qrid, date, indexTime, request);
            if (res.getSuccess() == true) {
                data = cacheService.getCache("PriceExtendCode" + qrid);
            } else {
                // error calc price extend
                return res;
            }
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

        codeRepository.updateCode(code);

        cacheService.setCache("remaining_" + uid, remaining - price);
        return new ResponseSuccess();
    }

    private int getRemainingUser(String idUser) throws SQLException {
        Integer remaining = cacheService.getCache("remaining_" + idUser);
        if (remaining != null) {
            return remaining;
        }

        return cashCustomerService.getRemaining(idUser);
    }

    public ResponseSuccess cancleCode(String qrid, HttpServletRequest request) throws BadRequestException, SQLException {
        Customer staffDataToken = (Customer) request.getAttribute("customerDataToken");
        String uid = staffDataToken.getUid();

        qrid = qrid.trim();
        if (qrid.equals("")) {
            throw new BadRequestException("Invalid code");
        }

        Code code = codeRepository.getInfo(uid, qrid);
        if (code == null) {
            throw new BadRequestException("Code not exist");
        }
        if (code.getCheckinAt() != 0 || code.getCancleAt() != 0) {
            throw new BadRequestException("Cannot cancle");
        }

        long now = Generate.getTimeStamp();
        code.setCancleAt(now);

        codeRepository.updateCode(code);
        return new ResponseSuccess();
    }
}
