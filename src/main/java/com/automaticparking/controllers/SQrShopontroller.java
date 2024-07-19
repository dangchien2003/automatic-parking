package com.automaticparking.controllers;

import com.automaticparking.services.SQrShopService;
import com.automaticparking.database.dto.CreateQrCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping("staff/shop-qr")
public class SQrShopontroller extends ResponseApi {

    private SQrShopService qrShopStaffService;

    @Autowired
    public SQrShopontroller(SQrShopService qrShopStaffService) {
        this.qrShopStaffService = qrShopStaffService;
    }

    @PostMapping("create")
    ResponseEntity<?> createQrCategory(@Valid @RequestBody CreateQrCategoryDto createData) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(qrShopStaffService.createQrCategory(createData));
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return serverError();
        }
    }
}
