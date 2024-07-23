package com.automaticparking.controllers;

import com.automaticparking.database.dto.ChangePasswordDto;
import com.automaticparking.database.dto.CreateStaffDto;
import com.automaticparking.database.dto.LoginDto;
import com.automaticparking.database.dto.UpdateStaffDto;
import com.automaticparking.services.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("api/staff")
@AllArgsConstructor
public class StaffController  {
    private StaffService staffService;

    @PostMapping("create-admin")
    ResponseEntity<?> createAdmin(@Valid @RequestBody CreateStaffDto createStaff) {
        return staffService.createAdmin(createStaff);
    }

    @PostMapping("login")
    ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        return staffService.login(loginDto, response);
    }

    @PostMapping("create")
    ResponseEntity<?> createStaff(@Valid @RequestBody CreateStaffDto createStaff, HttpServletRequest request) {
        return staffService.createStaff(createStaff, request);
    }

    @GetMapping("get-all")
    ResponseEntity<?> getAllStaff(HttpServletRequest request) {
        return staffService.getAllStaff(request);
    }

    @PatchMapping("lock/{sid}")
    ResponseEntity<?> lockStaff(@PathVariable String sid, HttpServletRequest request) {
        return staffService.lockStaff(sid, request);
    }

    @PatchMapping("unlock/{sid}")
    ResponseEntity<?> UnLockStaff(@PathVariable String sid, HttpServletRequest request) {
        return staffService.unLockStaff(sid, request);
    }

    @PatchMapping("change-password")
    ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePassword, HttpServletRequest request) {
        return staffService.changePassword(changePassword, request);
    }

    @PutMapping("update/{sid}")
    ResponseEntity<?> updateStaff(@Valid @RequestBody UpdateStaffDto updateStaff, @PathVariable String sid) {
        return staffService.updateStaff(updateStaff, sid);
    }


}
