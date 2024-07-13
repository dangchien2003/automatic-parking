package com.automaticparking.model.shopQr.staff;

import com.automaticparking.model.shopQr.staff.dto.CreateQrCategoryDto;
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
public class QrShopForStaffController extends ResponseApi {

    private QrShopForStaffService qrShopForStaffService;

    @Autowired
    public QrShopForStaffController(QrShopForStaffService qrShopForStaffService) {
        this.qrShopForStaffService = qrShopForStaffService;
    }

    @PostMapping("create")
    ResponseEntity<?> createQrCategory(@Valid @RequestBody CreateQrCategoryDto createData) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(qrShopForStaffService.createQrCategory(createData));
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return serverError();
        }
    }
}
