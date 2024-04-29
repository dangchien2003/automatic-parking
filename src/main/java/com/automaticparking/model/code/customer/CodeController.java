package com.automaticparking.model.code.customer;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.customer.CashCustomerService;
import com.automaticparking.model.code.customer.dto.BuyCodeDto;
import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopService;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("customer/code")
public class CodeController extends ResponseApi {

    private final QrShopService qrShopService = new QrShopService();
    private final CashCustomerService CashService = new CashCustomerService();
    private final CodeService codeService = new CodeService();


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

            // get history plus cash
            List<Cash> historyCash = CashService.getALlMyHistoryOk(uid);

            Integer totalMyCash =  0;
            for(Cash cash : historyCash) {
                totalMyCash += cash.getMoney();
            }

            // get history code use
            List<Code> myCode = codeService.getAllCodeUse(uid);
            Integer moneyUsed = 0;
            for(Code code : myCode) {
                moneyUsed += code.getPrice();
            }

            // get remaining(số dư)
            Integer remaining = totalMyCash - moneyUsed;

            if(remaining < 0) {
                System.out.println("Nhỏ hơn số tiền nạp");
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

            ResponseSuccess<Code> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = code;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
