package com.automaticparking.model.shopQr.staff;

import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopRepository;
import com.automaticparking.model.shopQr.staff.dto.CreateQrCategoryDto;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;

import java.sql.SQLException;

@Service
public class QrShopForStaffService extends ResponseApi {

    private final QrShopRepository qrShopRepository = new QrShopRepository();

    ResponseSuccess createQrCategory(CreateQrCategoryDto createData) throws SQLException {
        QrShop qr = new QrShop(createData.qrCategory, createData.price, createData.maxAge, 0);
        qrShopRepository.saveQrCategory(qr);
        return new ResponseSuccess(qr);
    }
}
