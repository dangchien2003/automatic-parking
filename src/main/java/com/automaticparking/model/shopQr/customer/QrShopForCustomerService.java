package com.automaticparking.model.shopQr.customer;

import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopRepository;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;

import java.sql.SQLException;
import java.util.List;

@Service
public class QrShopForCustomerService extends ResponseApi {
    private QrShopRepository qrShopRepository;

    @Autowired
    public QrShopForCustomerService(QrShopRepository qrShopRepository) {
        this.qrShopRepository = qrShopRepository;
    }

    ResponseSuccess getAllCodeOK() throws SQLException {
        return new ResponseSuccess(qrShopRepository.getAllCodeOk());
    }
}
