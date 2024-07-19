package com.automaticparking.services;

import com.automaticparking.repositorys.QrShopRepository;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import response.ResponseApi;

import java.sql.SQLException;

@Service
public class CQrShopService extends ResponseApi {
    private QrShopRepository qrShopRepository;

    @Autowired
    public CQrShopService(QrShopRepository qrShopRepository) {
        this.qrShopRepository = qrShopRepository;
    }

    public ResponseSuccess getAllCodeOK() throws SQLException {
        return new ResponseSuccess(qrShopRepository.getAllCodeOk());
    }
}
