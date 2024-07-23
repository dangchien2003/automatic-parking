package com.automaticparking.controllers;

import com.automaticparking.database.dto.CreateQrCategoryDto;
import com.automaticparking.services.SQrShopService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;

@RestController
@RequestMapping("api/staff/shop-qr")
@AllArgsConstructor
public class SQrShopontroller  {

    private SQrShopService qrShopStaffService;

    @PostMapping("create")
    ResponseEntity<?> createQrCategory(@Valid @RequestBody CreateQrCategoryDto createData) {
        return qrShopStaffService.createQrCategory(createData);
    }
}
