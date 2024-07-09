package com.automaticparking.model.staff;

import com.automaticparking.model.staff.dto.ChangePasswordDto;
import com.automaticparking.model.staff.dto.CreateStaffDto;
import com.automaticparking.model.staff.dto.LoginDto;
import com.automaticparking.model.staff.dto.UpdateStaffDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("staff")
public class StaffController {
    private StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

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
