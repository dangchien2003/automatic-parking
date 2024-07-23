package com.automaticparking.controllers;

import com.automaticparking.database.dto.InputMoneyDto;
import com.automaticparking.services.CCashService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("/customer/cash")
@AllArgsConstructor
public class CCashController  {
    private CCashService cashCustomerService;

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
