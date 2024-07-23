package com.automaticparking.controllers;

import com.automaticparking.database.dto.*;
import com.automaticparking.services.CustomerService;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("api/customer")
@AllArgsConstructor
public class CustomerController {
    private CustomerService customerService;

    @PostMapping("register")
    ResponseEntity<?> createAccount(@Valid @RequestBody RegisterDto registerDto) {
        return customerService.createAccount(registerDto);
    }

    @PatchMapping("accept-account")
    ResponseEntity<?> acceptAccount(@RequestBody Map<String, String> data) {
        return customerService.acceptAccount(data);
    }

    @PostMapping("login")
    ResponseEntity<?> login(@Valid @RequestBody LoginDto dataLogin, HttpServletResponse response) {
        return customerService.login(dataLogin, response);
    }

    @PostMapping("login/google")
    ResponseEntity<?> loginByGoogle(@Valid @RequestBody LoginGooleDto loginGooleDto, HttpServletResponse response) {
        return customerService.loginGoogle(loginGooleDto.getGoogleToken(), response);
    }

    @GetMapping("refresh")
    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        return customerService.refresh(request, response);
    }

    @PostMapping("logout")
    ResponseEntity<?> logout(HttpServletResponse response) {
        return customerService.logout(response);
    }

    @PostMapping("authentication")
    ResponseEntity<?> authen() {
        return new ResponseEntity(new ResponseSuccess(HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("forget")
    ResponseEntity<?> forget(@Valid @RequestBody ForgetPassword forgetPassword) {
        return customerService.forget(forgetPassword);
    }

    @GetMapping("forget/{forgetToken}")
    ResponseEntity<?> acceptForget(@Valid @PathVariable String forgetToken) {
        return customerService.acceptForget(forgetToken);
    }

    @GetMapping("me")
    ResponseEntity<?> acceptForget(HttpServletRequest request) {
        return customerService.getMyinfo(request);
    }

    @PatchMapping("change-password")
    ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dataPassword, HttpServletRequest request, HttpServletResponse response) {
        return customerService.changePassword(dataPassword, request, response);
    }

    @PostMapping("change-email")
    ResponseEntity<?> changeEmail(HttpServletRequest request, @Valid @RequestBody ChangeEmailDto dataChange) {
        return customerService.changeEmail(request, dataChange);
    }

    @PatchMapping("accept-change-email")
    ResponseEntity<?> acceptChangeEmail(@Valid @RequestBody AcceptChangeEmailDto data) {
        return customerService.acceptChangeEmail(data);
    }
}
