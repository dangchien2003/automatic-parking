package com.automaticparking.model.customer;


import com.automaticparking.model.customer.dto.ForgetPassword;
import com.automaticparking.model.customer.dto.RegisterDto;
import com.automaticparking.model.mailer.MailService;
import com.automaticparking.model.mailer.MailTemplate;
import com.automaticparking.model.mailer.Render;
import com.automaticparking.types.ResponseSuccess;
import encrypt.Hash;
import encrypt.JWT;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;
import util.CustomDotENV;
import util.Genarate;
import util.Random;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("customer")
public class CustomerController extends ResponseApi {
    private final CustomerService customerService = new CustomerService();
    private final MailService mailService;

    private final Render mailRender;
    @Autowired
    public CustomerController(MailService mailService, Render mailRender) {
        this.mailService = mailService;
        this.mailRender = mailRender;
    }

    @PostMapping("register")
    ResponseEntity<?> createAdmin(@Valid @RequestBody RegisterDto registerDto)  {
        try {
            Customer customer = new Customer();
            customer.setUid(Util.genarateUid());

            customer.setEmail(registerDto.email);

            customer.setLastLogin(Genarate.getTimeStamp());

            Hash hash = new Hash();
            customer.setPassword(hash.hash(registerDto.password));

            customer.setBlock(0);

            Boolean created = customerService.saveCustomer(customer);

            if(!created) {
                return badRequestApi("Email already exist");
            }

            ResponseSuccess<Customer> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("login")
    ResponseEntity<?> login(@Valid @RequestBody RegisterDto dataLogin, HttpServletResponse response) {
        try {
            Customer customer = customerService.getCustomerByEmail(dataLogin.email);

            if(customer == null) {
                return badRequestApi("Email not exist");
            }

            Hash hash = new Hash();
            if(!hash.compareHash(dataLogin.password, customer.getPassword())) {
                return badRequestApi("Incorrect password");
            }

            if(customer.getBlock() == 1) {
                return Error(HttpStatus.UNAUTHORIZED, "Account has been locked");
            }

            JWT<Customer> jwt = new JWT<>();
            String utoken = jwt.createJWT(customer, Long.parseLong(CustomDotENV.get("TIME_SECOND_TOKEN")));

            Map<String, String> cookies = new HashMap<>();
            cookies.put("UToken", utoken);

            Cookie cookie = new Cookie("UToken", utoken);
            cookie.setAttribute("HttpOnly", "True");
            cookie.setAttribute("SameSite", "None");
            cookie.setAttribute("Partitioned", "True");

            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            ResponseSuccess<Customer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.cookies = cookies;
            responseSuccess.data = customer;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }


    @PostMapping("forget")
    ResponseEntity<?> forget(@Valid @RequestBody ForgetPassword forgetPassword) {
        try {

            Customer customer = customerService.getCustomerByEmail(forgetPassword.email);

            if(customer == null) {
                return badRequestApi("Email not exist");
            }

            // set data token
            forgetPassword.lastLogin = customer.getLastLogin();

            JWT<ForgetPassword> jwt = new JWT<>();
            String forgetToken = jwt.createJWT(forgetPassword, Long.parseLong(CustomDotENV.get("FORGET_PASSWORD_SECOND_TOKEN")));

            // get template
            String html = mailRender.customerForget(forgetToken);

            // set conten mail
            MailTemplate mailTemplate = new MailTemplate();
            mailTemplate.setSubject("Quên mật khẩu");
            mailTemplate.setTo(forgetPassword.email);
            mailTemplate.setHtml(html);

            mailService.sendEmail(mailTemplate);
            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("forget/{forgetToken}")
    ResponseEntity<?> acceptForget(@Valid @PathVariable String forgetToken) {
        try {
            JWT<ForgetPassword> jwt = new JWT<>();
            Claims dataToken = jwt.decodeJWT(forgetToken);

            if(dataToken == null) {
                return badRequestApi("Invalid token");
            }

            Map<String, String> dataForget = Genarate.getMapFromJson(dataToken.getSubject());

            String email = dataForget.get("email");

            if(email.trim().isEmpty()) {
                return badRequestApi("Invalid email");
            }

            Customer customer = customerService.getCustomerByEmail(email);
            if(customer == null) {
                return badRequestApi("Email not exist");
            }

            if(dataForget.get("lastLogin") == null || customer.getLastLogin() != Long.parseLong(dataForget.get("lastLogin"))) {
                return badRequestApi("Token has expired");
            }

            String newPassword = Random.generateRandomString(10);

            Hash hash = new Hash();
            String hashNewPassword = hash.hash(newPassword);

            customer.setPassword(hashNewPassword);
            customer.setLastLogin(Genarate.getTimeStamp());
            Boolean updated = customerService.updateCustomer(customer);
            if(!updated) {
                throw new Exception("Error update");
            }

            String html = mailRender.customerNewPassword(newPassword);

            MailTemplate mailTemplate = new MailTemplate();
            mailTemplate.setSubject("Cập nhật mật khẩu");
            mailTemplate.setHtml(html);
            mailTemplate.setTo(email);

            mailService.sendEmail(mailTemplate);

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }



}
