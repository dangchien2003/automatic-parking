package com.automaticparking.model.cash.staff;

import com.automaticparking.model.cash.staff.dto.ApproveDto;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;

import java.sql.SQLException;

@RestController
@RequestMapping("staff")
public class CashStaffController extends ResponseApi {
    private CashStaffService cashStaffService;

    @Autowired
    public CashStaffController(CashStaffService cashStaffService) {
        this.cashStaffService = cashStaffService;
    }

    @GetMapping("cash/not-approve/get-all")
    ResponseEntity<?> getAllCashNotApprove() {
        try {
            return ResponseEntity.ok(cashStaffService.getAllCashNotApprove());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }

    }

    @PatchMapping("cash/approve")
    ResponseEntity<?> approveCash(@Valid @RequestBody ApproveDto approve, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(cashStaffService.approveCash(approve, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return serverError();
        }
    }
}
