package com.automaticparking.services;

import com.automaticparking.Repositorys.QRShopRepository;
import com.automaticparking.database.dto.CreateQrCategoryDto;
import com.automaticparking.database.entity.QrShop;
import com.automaticparking.types.ResponseSuccess;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SQrShopService {

    private QRShopRepository qrShopRepository;

    public ResponseEntity<Object> createQrCategory(CreateQrCategoryDto createData) {
        QrShop qr = new QrShop(createData.qrCategory, createData.price, createData.maxAge, 0);
        qrShopRepository.save(qr);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }
}
