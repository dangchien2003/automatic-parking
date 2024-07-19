package com.automaticparking.controllers;

import com.automaticparking.services.CQrShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import java.sql.SQLException;

@RestController
@RequestMapping("customer/shop-qr")
public class CQrShopController extends ResponseApi {
    private CQrShopService qrShopForCustomerService;

    @Autowired
    public CQrShopController(CQrShopService qrShopForCustomerService) {
        this.qrShopForCustomerService = qrShopForCustomerService;
    }

    @GetMapping("all")
    ResponseEntity<?> getAllCodeOK() {
        try {
            return ResponseEntity.ok(qrShopForCustomerService.getAllCodeOK());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }
}
