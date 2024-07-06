package com.automaticparking.model.code.customer;

import com.automaticparking.model.cache.CacheService;
import com.automaticparking.model.cash.customer.CashCustomerRepository;
import com.automaticparking.model.cash.customer.CashCustomerService;
import com.automaticparking.model.code.customer.dto.BuyCodeDto;
import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopRepository;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;
import util.Genarate;
import util.Json;
import util.KeyCache;
import util.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


@Service
public class CodeService extends ResponseApi {

    private QrShopRepository qrShopService;
    private CodeRepository codeRepository;
    private CashCustomerRepository cashCustomerService;
    private CacheService cacheService;
    private Executor asyncExecutor;

    @Autowired
    public CodeService(Executor asyncExecutor, CacheService cacheService, CashCustomerRepository cashCustomerService, CodeRepository codeRepository, QrShopRepository qrShopService) {
        this.asyncExecutor = asyncExecutor;
        this.cacheService = cacheService;
        this.cashCustomerService = cashCustomerService;
        this.codeRepository = codeRepository;
        this.qrShopService = qrShopService;
    }

    public ResponseEntity<?> buyCode(BuyCodeDto buyCode, HttpServletRequest request) {
        try {
            Map<String, String> customerDataToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            // get qr info
            QrShop qr = qrShopService.getOneQrById(buyCode.qrCategory);

            if (qr == null) {
                return badRequestApi("Qr not exist");
            }

            // get remaining(số dư)
            Integer remaining = cashCustomerService.getRemaining(uid);

            if (remaining < 0) {
                System.out.println("Số dư nhỏ hơn 0");
            }

            if (remaining < qr.getPrice()) {
                return badRequestApi("The balance is not enough, please add more money");
            }

            Integer discount = 0;
            // mua code
            Util codeUtil = new Util();
            Code code = codeUtil.setCode(uid, qr, discount);

            Boolean saved = codeRepository.saveCode(code);

            if (!saved) {
                throw new Exception("Error save code");
            }

            // reset cache remaining
            asyncExecutor.execute(() -> {
                String keyCache = "remaining_" + uid;
                Integer cacheRemaining = cacheService.getCache(keyCache);
                if (cacheRemaining != null && cacheRemaining > 0) {
                    cacheService.setCache(keyCache, cacheRemaining - code.getPrice());
                }
            });

            ResponseSuccess<Code> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = code;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public ResponseEntity<?> getBoughtCode(HttpServletRequest request, String quantity) {
        try {
            Map<String, String> customerDataToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            Integer quantityLimit;
            try {
                quantityLimit = Integer.parseInt(quantity.trim());
            } catch (Exception e) {
                quantityLimit = 1000000;
            }

            List<Code> boughtCode = codeRepository.allBoughtCode(uid, quantityLimit);
            if (boughtCode == null) {
                throw new Exception("Error get code");
            }
            ResponseSuccess<List<Code>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = boughtCode;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }


    public ResponseEntity<?> getInfoCode(String qrid, HttpServletRequest request) {
        try {

            if (qrid == null) {
                return badRequestApi("Invalid qr");
            }

            Map<String, String> customerDataToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            Code code = codeRepository.getInfo(uid, qrid);

            if (code == null) {
                return Error(HttpStatus.NOT_FOUND, "Not found");
            }

            if (code.getAcceptBy() != null) {
                code.setAcceptBy("1");
            }

            ResponseSuccess<Code> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = code;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public ResponseEntity<?> getContenQr(String qrid, HttpServletRequest request) {
        try {
            if (qrid == null || qrid.trim().isEmpty()) {
                return badRequestApi("Invalid qr");
            }

            Map<String, String> customerDataToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            String keyCache = KeyCache.getKeyContentQr(uid, qrid);
            String contentQr = cacheService.getCache(keyCache);

            if (contentQr == null) {
                Code code = codeRepository.getInfo(uid, qrid);

                if (code == null) {
                    return Error(HttpStatus.NOT_FOUND, "Not found");
                }

                if (code.getCancleAt() != 0) {
                    return badRequestApi("Cancled");
                }

                if (code.getCheckoutAt() != 0) {
                    return badRequestApi("Checkouted");
                }

                // get time expire
                Long exprireAt = code.getExpireAt();

                if (exprireAt <= Genarate.getTimeStamp()) {
                    return badRequestApi("expired");
                }


                ContentQr content = new ContentQr(code.getQrid(), exprireAt);

                if (code.getCheckinAt() == 0) {
                    content.setAcceptBot(1);
                } else {
                    content.setAcceptBot(2);
                }

                Json<ContentQr> json = new Json<>();
                contentQr = json.convertToJson(content);

                if (contentQr == null) {
                    throw new Exception("Error genarate qr");
                }

                if (!cacheService.setCache(keyCache, contentQr)) {
                    System.out.println("Error set cache");
                }
            }

            ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = contentQr;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public ResponseEntity<?> calcPriceExtendCode(String qrid, String date, int indexTime, HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = staffDataToken.get("uid");

            if (date.trim().isEmpty()) {
                return badRequestApi("Invalid date");
            }

            long timestamp = Timestamp.convertDateToTimestamp(date, "dd-MM-yyyy");

            if (timestamp <= 0) {
                return badRequestApi("Invalid date");
            }

            if (1 > indexTime || indexTime > 4) {
                return badRequestApi("Invalid time");
            }

            Code code = codeRepository.getInfo(uid, qrid);
            if (code == null) {
                return badRequestApi("Invalid code");
            }

            long now = Genarate.getTimeStamp();

            if (code.getCheckinAt() == 0 || code.getCheckoutAt() != 0 || now < code.getExpireAt()) {
                return badRequestApi("Code cannot be renewed");
            }

            long newExpire = timestamp + indexTime * 21600000;
            if (now > newExpire - 15 * 60 * 1000) {
                return badRequestApi("The time must be at least 15 minutes greater than the current time");
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
            ResponseSuccess<Map<String, Object>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = dataRes;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public ResponseEntity<?> extendCode(String qrid, String date, int indexTime, HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = staffDataToken.get("uid");

            Map<String, Object> data = cacheService.getCache("PriceExtendCode" + qrid);
            if (data == null || !data.get("date").equals(date) || !data.get("indexTime").equals(indexTime)) {
                ResponseEntity<?> res = calcPriceExtendCode(qrid, date, indexTime, request);
                if (res.getStatusCode() == HttpStatus.OK) {
                    data = cacheService.getCache("PriceExtendCode" + qrid);
                } else {
                    return res;
                }
            }

            int price = (int) data.get("price");
            int remaining = getRemainingUser(uid);
            if (remaining < price) {
                return badRequestApi("Not enough money");
            }

            long newExpire = (long) data.get("dateTime");

            Code code = (Code) data.get("code");
            code.setExpireAt(newExpire);
            code.setPriceExtend(code.getPriceExtend() + price);
            code.setTimesExtend(code.getTimesExtend() + 1);

            boolean updated = codeRepository.updateCode(code);
            if (!updated) {
                throw new Exception("Update error");
            }

            cacheService.setCache("remaining_" + uid, remaining - price);
            ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    private int getRemainingUser(String idUser) {
        Integer remaining = cacheService.getCache("remaining_" + idUser);
        if (remaining != null) {
            return remaining;
        }

        return cashCustomerService.getRemaining(idUser);
    }

    ResponseEntity<?> cancleCode(String qrid, HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = staffDataToken.get("uid");

            qrid = qrid.trim();
            if (qrid.equals("")) {
                return badRequestApi("Invalid code");
            }

            Code code = codeRepository.getInfo(uid, qrid);
            if (code == null) {
                return badRequestApi("Code not exist");
            }
            if (code.getCheckinAt() != 0 || code.getCancleAt() != 0) {
                return badRequestApi("Cannot cancle");
            }

            long now = Genarate.getTimeStamp();
            code.setCancleAt(now);

            boolean updated = codeRepository.updateCode(code);
            if (!updated) {
                throw new Exception("Update error");
            }

            ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }
}
