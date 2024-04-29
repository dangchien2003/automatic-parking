package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.customer.dto.InputMoneyDto;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;
import util.Genarate;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("customer")
public class CashCustomerController extends ResponseApi {
    private CashCustomerService cashCustomerService = new CashCustomerService();

    @PostMapping("cash/input-money")
    ResponseEntity<?> inputMoney(@Valid @RequestBody InputMoneyDto inputMoney,  HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("customerDataToken");

            String uid = staffDataToken.get("uid");
            Cash cash = new Cash();
            cash.setUid(uid);
            cash.setMoney(inputMoney.money);
            cash.setCashAt(Genarate.getTimeStamp());
            cash.setStringCode(uid);

            Boolean create = cashCustomerService.saveCashHistory(cash);

            if(!create) {
                throw new Exception("Error create history. Please contact for us");
            }

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
