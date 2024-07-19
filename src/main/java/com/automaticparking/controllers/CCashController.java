package com.automaticparking.controllers;

import com.automaticparking.services.CCashService;
import com.automaticparking.database.dto.InputMoneyDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;

import javax.validation.Valid;

@RestController
@RequestMapping("customer/cash")
public class CCashController extends ResponseApi {
    private CCashService cashCustomerService;

    @Autowired
    public CCashController(CCashService cashCustomerService) {
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
