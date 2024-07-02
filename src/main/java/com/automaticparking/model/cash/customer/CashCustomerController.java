package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cash.customer.dto.InputMoneyDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("customer/cash")
public class CashCustomerController {
    private CashCustomerService cashCustomerService;

    @Autowired
    public CashCustomerController(CashCustomerService cashCustomerService) {
        this.cashCustomerService = cashCustomerService;
    }

    @PostMapping("input-money")
    ResponseEntity<?> inputMoney(@Valid @RequestBody InputMoneyDto inputMoney, HttpServletRequest request) {
        return cashCustomerService.inputMoney(inputMoney, request);
    }

    @GetMapping("all")
    ResponseEntity<?> allMyHistory(HttpServletRequest request) {
        return cashCustomerService.allMyHistory(request);
    }

    @GetMapping("remaining")
    ResponseEntity<?> getMyRemaining(HttpServletRequest request) {
        return cashCustomerService.getMyRemaining(request);
    }
}
