package com.automaticparking.model.customer;

import com.automaticparking.model.customer.dto.*;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;

import javax.naming.AuthenticationException;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("customer")
public class CustomerController extends ResponseApi {
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("register")
    ResponseEntity<?> createAdmin(@Valid @RequestBody RegisterDto registerDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createAdmin(registerDto));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PatchMapping("accept-account")
    ResponseEntity<?> acceptAccount(@RequestBody Map<String, String> data) {
        try {
            return ResponseEntity.ok(customerService.acceptAccount(data));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }


    @PostMapping("login")
    ResponseEntity<?> login(@Valid @RequestBody RegisterDto dataLogin, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(customerService.login(dataLogin, response));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (AuthenticationException e) {
            return error(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @GetMapping("refresh/tok")
    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(customerService.refresh(request, response));
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PostMapping("logout")
    ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            return ResponseEntity.ok(customerService.logout(response));
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PostMapping("authentication")
    ResponseEntity<?> authen() {
        return ResponseEntity.ok(new ResponseSuccess());
    }

    @PostMapping("forget")
    ResponseEntity<?> forget(@Valid @RequestBody ForgetPassword forgetPassword) {
        try {
            return ResponseEntity.ok(customerService.forget(forgetPassword));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @GetMapping("forget/{forgetToken}")
    ResponseEntity<?> acceptForget(@Valid @PathVariable String forgetToken) {
        try {
            return ResponseEntity.ok(customerService.acceptForget(forgetToken));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @GetMapping("me")
    ResponseEntity<?> acceptForget(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(customerService.getMyinfo(request));
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PatchMapping("change-password")
    ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dataPassword, HttpServletRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(customerService.changePassword(dataPassword, request, response));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @PostMapping("change-email")
    ResponseEntity<?> changeEmail(HttpServletRequest request, @Valid @RequestBody ChangeEmailDto dataChange) {
        try {
            return ResponseEntity.ok(customerService.changeEmail(request, dataChange));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return serverError();
        }
    }

    @PatchMapping("accept-change-email")
    ResponseEntity<?> acceptChangeEmail(@Valid @RequestBody AcceptChangeEmailDto data) {
        try {
            return ResponseEntity.ok(customerService.acceptChangeEmail(data));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }
}
