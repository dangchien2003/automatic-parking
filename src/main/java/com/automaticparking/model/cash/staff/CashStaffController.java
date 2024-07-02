package com.automaticparking.model.cash.staff;

import com.automaticparking.model.cash.staff.dto.ApproveDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("staff")
public class CashStaffController {
    private CashStaffService cashStaffService;

    @Autowired
    public CashStaffController(CashStaffService cashStaffService) {
        this.cashStaffService = cashStaffService;
    }

    @GetMapping("cash/not-approve/get-all")
    ResponseEntity<?> getAllCashNotApprove() {
        return cashStaffService.getAllCashNotApprove();
    }

    @PatchMapping("cash/approve")
    ResponseEntity<?> approveCash(@Valid @RequestBody ApproveDto approve, HttpServletRequest request) {
        return cashStaffService.approveCash(approve, request);
    }
}
