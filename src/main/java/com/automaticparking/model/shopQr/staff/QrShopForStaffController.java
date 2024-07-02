package com.automaticparking.model.shopQr.staff;

import com.automaticparking.model.shopQr.staff.dto.CreateQrCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("staff/shop-qr")
public class QrShopForStaffController {

    private QrShopForStaffService qrShopForStaffService;

    @Autowired
    public QrShopForStaffController(QrShopForStaffService qrShopForStaffService) {
        this.qrShopForStaffService = qrShopForStaffService;
    }

    @PostMapping("create")
    ResponseEntity<?> createQrCategory(@Valid @RequestBody CreateQrCategoryDto createData) {
        return qrShopForStaffService.createQrCategory(createData);
    }
}
