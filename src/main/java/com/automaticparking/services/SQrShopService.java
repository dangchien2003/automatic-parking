package com.automaticparking.services;

import com.automaticparking.database.entity.QrShop;
import com.automaticparking.repositorys.QrShopRepository;
import com.automaticparking.database.dto.CreateQrCategoryDto;
import com.automaticparking.types.BaseResponse;
import com.automaticparking.types.ResponseSuccess;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import exception.ConflictException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;

import java.sql.SQLException;

@Service
public class SQrShopService extends BaseResponse {

    private final QrShopRepository qrShopRepository = new QrShopRepository();

    public ResponseEntity<Object> createQrCategory(CreateQrCategoryDto createData) throws SQLException {
        QrShop qr = new QrShop(createData.qrCategory, createData.price, createData.maxAge, 0);
        qrShopRepository.saveQrCategory(qr);
        return ResponseEntity.status(HttpStatus.CREATED).body(getResponse(HttpStatus.CREATED.value(), qr));
    }
}
