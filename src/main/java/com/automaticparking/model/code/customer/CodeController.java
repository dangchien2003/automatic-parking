package com.automaticparking.model.code.customer;

import com.automaticparking.model.cache.CacheService;
import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.customer.CashCustomerService;
import com.automaticparking.model.code.customer.dto.BuyCodeDto;
import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopService;
import com.automaticparking.types.ResponseSuccess;
import encrypt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;
import util.Genarate;
import util.KeyCache;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("customer/code")
public class CodeController extends ResponseApi {

    private final QrShopService qrShopService = new QrShopService();
    private final CodeService codeService = new CodeService();
    private final CashCustomerService cashCustomerService = new CashCustomerService();
    private CacheService cacheService;
    private Executor asyncExecutor;

    @Autowired
    public CodeController( Executor asyncExecutor, CacheService cacheService) {
        this.asyncExecutor = asyncExecutor;
        this.cacheService = cacheService;
    }


    @PostMapping("buy")
    ResponseEntity<?> buyCode(@Valid @RequestBody BuyCodeDto buyCode, HttpServletRequest request) {
        try {
            Map<String, String> customerDataToken = (Map <String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            // get qr info
            QrShop qr = qrShopService.getOneQrById(buyCode.qrCategory);

            if(qr == null) {
                return badRequestApi("Qr not exist");
            }

            // get remaining(số dư)
            Integer remaining = cashCustomerService.getRemaining(uid);

            if(remaining < 0) {
                System.out.println("Số dư nhỏ hơn 0");
            }

            if(remaining < qr.getPrice()) {
                return badRequestApi("The balance is not enough, please add more money");
            }

            Integer discount = 0;
            // mua code
            Util codeUtil = new Util();
            Code code = codeUtil.setCode(uid, qr, discount);

            Boolean saved = codeService.saveCode(code);

            if(!saved) {
                throw new Exception("Error save code");
            }

            // reset cache remaining
            asyncExecutor.execute(() -> {
                String keyCache = "remaining_" + uid;
                Integer cacheRemaining = cacheService.getCache(keyCache);
                if(cacheRemaining != null && cacheRemaining > 0) {
                    cacheService.setCache(keyCache, cacheRemaining - code.getPrice());
                }
            });
            
            ResponseSuccess<Code> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = code;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
    @GetMapping("bought")
    ResponseEntity<?> getBoughtCode(HttpServletRequest request, @RequestParam(required = false) String quantity) {
        try {
            Map<String, String> customerDataToken = (Map <String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            Integer quantityLimit;
            try{
                quantityLimit = Integer.parseInt(quantity.trim());
            }catch (Exception e){
                quantityLimit = 1000000;
            }

            List<Code> boughtCode = codeService.allBoughtCode(uid, quantityLimit);
            if(boughtCode == null) {
                throw new Exception("Error get code");
            }
            ResponseSuccess<List<Code>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = boughtCode;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("i")
    ResponseEntity<?> getInfoCode(@RequestParam("qrid") String qrid, HttpServletRequest request) {
        try {

            if(qrid == null) {
                return badRequestApi("Invalid qr");
            }

            Map<String, String> customerDataToken = (Map <String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            Code code = codeService.getInfo(uid, qrid);

            if(code == null) {
                return Error(HttpStatus.NOT_FOUND, "Not found");
            }

            if(code.getAcceptBy() != null) {
                code.setAcceptBy("1");
            }

            ResponseSuccess<Code> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = code;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("qr/{qrid}")
    ResponseEntity<?> getContenQr(@PathVariable String qrid, HttpServletRequest request) {
        try {
            if(qrid == null || qrid.trim().isEmpty()) {
                return badRequestApi("Invalid qr");
            }

            Map<String, String> customerDataToken = (Map <String, String>) request.getAttribute("customerDataToken");
            String uid = customerDataToken.get("uid");

            String keyCache = KeyCache.getKeyContentQr(uid, qrid);
            String contentQr = cacheService.getCache(keyCache);

            if(contentQr == null) {
                Code code = codeService.getInfo(uid, qrid);

                if(code == null) {
                    return Error(HttpStatus.NOT_FOUND, "Not found");
                }

                if(code.getCancleAt() != null) {
                    return badRequestApi("Cancled");
                }

                if(code.getCheckoutAt() != null) {
                    return badRequestApi("Checkouted");
                }

                // get time expire
                Long exprireAt = null;
                if(code.getCheckinAt() != null) {
                    exprireAt = code.getExpireAt() + 24 * 60 * 60 * 1000;
                }else {
                    exprireAt = code.getExpireAt();
                }

                if(exprireAt <= Genarate.getTimeStamp()) {
                    return badRequestApi("expired");
                }


                Long now = Genarate.getTimeStamp();
                ContentQr content = new ContentQr(code.getQrid(), now);

                if(code.getCheckinAt() == null) {
                    content.setAcceptBot(1);
                }else {
                    content.setAcceptBot(2);
                }

                JWT<ContentQr> jwt = new JWT<>();
                contentQr = jwt.createJWT(content, (exprireAt - now) / 1000);

                if(contentQr == null) {
                    throw new Exception("Error genarate qr");
                }

                if(!cacheService.setCache(keyCache, contentQr)) {
                    System.out.println("Error set cache");
                }
            }

            ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = contentQr;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

}
