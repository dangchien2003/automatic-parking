package com.automaticparking.controllers;

import com.automaticparking.services.CQrShopService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/customer/shop-qr")
@AllArgsConstructor
public class CQrShopController  {
    private CQrShopService cQrShopService;

    @GetMapping("all")
    ResponseEntity<?> getAllCodeOK() {
        return cQrShopService.getAllCodeOK();
    }
}
