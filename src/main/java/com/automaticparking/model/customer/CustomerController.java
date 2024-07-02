package com.automaticparking.model.customer;

import com.automaticparking.model.customer.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("customer")
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("register")
    ResponseEntity<?> createAdmin(@Valid @RequestBody RegisterDto registerDto) {
        return customerService.createAdmin(registerDto);
    }

    @PatchMapping("accept-account")
    ResponseEntity<?> acceptAccount(@RequestBody Map<String, String> data) {
        return customerService.acceptAccount(data);
    }


    @PostMapping("login")
    ResponseEntity<?> login(@Valid @RequestBody RegisterDto dataLogin, HttpServletResponse response) {
        return customerService.login(dataLogin, response);
    }

    @GetMapping("refresh/tok")
    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        return customerService.refresh(request, response);
    }

    @PostMapping("logout")
    ResponseEntity<?> logout(HttpServletResponse response) {
        return customerService.logout(response);
    }

    @PostMapping("authentication")
    ResponseEntity<?> authen() {
        return customerService.authen();
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
        return customerService.acceptForget(request);
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
