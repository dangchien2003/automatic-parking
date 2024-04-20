package com.automaticparking.model.cash;

import com.automaticparking.types.ResponseSuccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.ResponseApi;

import java.util.List;
@RestController
@RequestMapping("cash")
public class CashController extends ResponseApi {
    private final CashService cashService = new CashService();
    @GetMapping("get-all")
    ResponseEntity<?> getAllCash() {
        try {
            List<Cash> cashs = cashService.getAllCashNotApproved();
            ResponseSuccess<List<Cash>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = cashs;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
