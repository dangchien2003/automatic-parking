package com.automaticparking.controllers;

import com.automaticparking.database.dto.ChangePasswordDto;
import com.automaticparking.database.dto.CreateStaffDto;
import com.automaticparking.database.dto.LoginDto;
import com.automaticparking.database.dto.UpdateStaffDto;
import com.automaticparking.services.StaffService;
import exception.ConflictException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;

import javax.naming.AuthenticationException;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@RestController
@RequestMapping("staff")
public class StaffController extends ResponseApi {
    private StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("create-admin")
    ResponseEntity<?> createAdmin(@Valid @RequestBody CreateStaffDto createStaff) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createAdmin(createStaff));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PostMapping("login")
    ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(staffService.login(loginDto, response));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthenticationException e) {
            return error(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PostMapping("create")
    ResponseEntity<?> createStaff(@Valid @RequestBody CreateStaffDto createStaff, HttpServletRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createStaff(createStaff, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @GetMapping("get-all")
    ResponseEntity<?> getAllStaff(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(staffService.getAllStaff(request));
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PatchMapping("lock/{sid}")
    ResponseEntity<?> lockStaff(@PathVariable String sid, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(staffService.lockStaff(sid, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PatchMapping("unlock/{sid}")
    ResponseEntity<?> UnLockStaff(@PathVariable String sid, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(staffService.unLockStaff(sid, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PatchMapping("change-password")
    ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePassword, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(staffService.changePassword(changePassword, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PutMapping("update/{sid}")
    ResponseEntity<?> updateStaff(@Valid @RequestBody UpdateStaffDto updateStaff, @PathVariable String sid) {
        try {
            return ResponseEntity.ok(staffService.updateStaff(updateStaff, sid));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Query error");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }


}
