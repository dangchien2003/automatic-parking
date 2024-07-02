package com.automaticparking.model.shopQr.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customer/shop-qr")
public class QrShopForCustomerController {
    private QrShopForCustomerService qrShopForCustomerService;

    @Autowired
    public QrShopForCustomerController(QrShopForCustomerService qrShopForCustomerService) {
        this.qrShopForCustomerService = qrShopForCustomerService;
    }

    @GetMapping("all")
    ResponseEntity<?> getAllCodeOK() {
        return qrShopForCustomerService.getAllCodeOK();
    }
}
