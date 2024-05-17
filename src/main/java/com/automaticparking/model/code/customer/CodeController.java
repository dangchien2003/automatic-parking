package com.automaticparking.model.code.customer;

import com.automaticparking.model.cache.CacheService;
import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.customer.CashCustomerService;
import com.automaticparking.model.code.customer.dto.BuyCodeDto;
import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopService;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;

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
}
