package com.automaticparking.model.shopQr.staff;

import com.automaticparking.model.shopQr.QrShop;
import com.automaticparking.model.shopQr.QrShopRepository;
import com.automaticparking.model.shopQr.staff.dto.CreateQrCategoryDto;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import response.ResponseApi;

@Service
public class QrShopForStaffService extends ResponseApi {

    private final QrShopRepository qrShopRepository = new QrShopRepository();

    ResponseEntity<?> createQrCategory(CreateQrCategoryDto createData) {
        try {
            QrShop qr = new QrShop();
            qr.setQrCategory(createData.qrCategory);
            qr.setPrice(createData.price);
            qr.setMaxAge(createData.maxAge);
            qr.setHide(0);

            Boolean created = qrShopRepository.saveQrCategory(qr);

            if (!created) {
                throw new Exception("Error save qr");
            }

            ResponseSuccess<QrShop> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = qr;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
