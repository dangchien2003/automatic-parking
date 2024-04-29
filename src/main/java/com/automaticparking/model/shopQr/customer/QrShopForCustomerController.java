package com.automaticparking.model.shopQr.customer;

import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopService;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import java.util.List;

@RestController
@RequestMapping("customer/shop-qr")
public class QrShopForCustomerController extends ResponseApi {
    private final QrShopService qrShopService = new QrShopService();
    @GetMapping("all")
    ResponseEntity<?> getAllCodeOK() {
        try {
            List<QrShop> listCodeOk = qrShopService.getAllCodeOk();
            ResponseSuccess<List<QrShop>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = listCodeOk;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }

    }
}
