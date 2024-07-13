package com.automaticparking.model.cash.customer;

import com.automaticparking.model.cash.customer.dto.InputMoneyDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping("customer/cash")
public class CashCustomerController extends ResponseApi {
    private CashCustomerService cashCustomerService;

    @Autowired
    public CashCustomerController(CashCustomerService cashCustomerService) {
        this.cashCustomerService = cashCustomerService;
    }

    @PostMapping("input-money")
    ResponseEntity<?> inputMoney(@Valid @RequestBody InputMoneyDto inputMoney, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(cashCustomerService.inputMoney(inputMoney, request));
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("all")
    ResponseEntity<?> allMyHistory(HttpServletRequest request) {

        try {
            return ResponseEntity.ok(cashCustomerService.allMyHistory(request));
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @GetMapping("remaining")
    ResponseEntity<?> getMyRemaining(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(cashCustomerService.getMyRemaining(request));
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }

    }
}
