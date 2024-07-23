package com.automaticparking.controllers;

import com.automaticparking.database.dto.ApproveDto;
import com.automaticparking.services.SCashService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/staff")
@AllArgsConstructor
public class SCashController  {
    private SCashService cashStaffService;

    @GetMapping("cash/not-approve/get-all")
    ResponseEntity<?> getAllCashNotApprove() {
        return cashStaffService.getAllCashNotApprove();
    }

    @PatchMapping("cash/approve")
    ResponseEntity<?> approveCash(@Valid @RequestBody ApproveDto approve, HttpServletRequest request) {
        return cashStaffService.approveCash(approve, request);
    }
}
