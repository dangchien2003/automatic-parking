package com.automaticparking.model.shopQr;

import com.automaticparking.model.shopQr.dto.CreateQrCategoryDto;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import javax.validation.Valid;

@RestController
@RequestMapping("staff/code")
public class QrShopController extends ResponseApi {

    private final QrShopService qrShopService = new QrShopService();
    @PostMapping("create")
    ResponseEntity<?> createQrCategory(@Valid @RequestBody CreateQrCategoryDto createData) {
        try {
            QrShop qr = new QrShop();
            qr.setQrCategory(createData.qrCategory);
            qr.setPrice(createData.price);
            qr.setMaxAge(createData.maxAge);

            Boolean created = qrShopService.saveQrCategory(qr);

            if(!created) {
                throw new Exception("Error save qr");
            }

            ResponseSuccess<QrShop> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = qr;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
