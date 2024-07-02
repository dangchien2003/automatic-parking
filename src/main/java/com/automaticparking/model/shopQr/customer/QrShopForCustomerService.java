package com.automaticparking.model.shopQr.customer;

import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopRepository;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;

import java.util.List;

@Service
public class QrShopForCustomerService extends ResponseApi {
    private QrShopRepository qrShopRepository;

    @Autowired
    public QrShopForCustomerService(QrShopRepository qrShopRepository) {
        this.qrShopRepository = qrShopRepository;
    }

    ResponseEntity<?> getAllCodeOK() {
        try {
            List<QrShop> listCodeOk = qrShopRepository.getAllCodeOk();
            ResponseSuccess<List<QrShop>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = listCodeOk;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }

    }
}
