package com.automaticparking.model.code.customer;

import com.automaticparking.model.cache.CacheService;
import com.automaticparking.model.cash.customer.CashCustomerRepository;
import com.automaticparking.model.code.customer.dto.BuyCodeDto;
import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopRepository;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;
import util.Genarate;
import util.Json;
import util.KeyCache;

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

                if (code.getCancleAt() != null) {
                    return badRequestApi("Cancled");
                }

                if (code.getCheckoutAt() != null) {
                    return badRequestApi("Checkouted");
                }

                // get time expire
                Long exprireAt = null;
                if (code.getCheckinAt() != null) {
                    exprireAt = code.getExpireAt() + 24 * 60 * 60 * 1000;
                } else {
                    exprireAt = code.getExpireAt();
                }

                if (exprireAt <= Genarate.getTimeStamp()) {
                    return badRequestApi("expired");
                }


                ContentQr content = new ContentQr(code.getQrid(), exprireAt);

                if (code.getCheckinAt() == null) {
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

}
