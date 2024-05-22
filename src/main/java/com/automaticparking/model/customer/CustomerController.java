package com.automaticparking.model.customer;


import com.automaticparking.model.cash.customer.CashCustomerService;
import com.automaticparking.model.code.customer.CodeService;
import com.automaticparking.model.customer.dto.*;
import com.automaticparking.model.mailer.MailService;
import com.automaticparking.model.mailer.MailTemplate;
import com.automaticparking.model.mailer.Render;
import com.automaticparking.types.ResponseSuccess;
import encrypt.Hash;
import encrypt.JWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;
import util.CustomDotENV;
import util.Genarate;
import util.Random;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

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
            Long now = Genarate.getTimeStamp();
            customer.setLastLogin(now);
            customer.setCreateAt(now);

            Hash hash = new Hash();
            customer.setPassword(hash.hash(registerDto.password));

            customer.setBlock(0);

            Boolean created = customerService.saveCustomer(customer);

            if(!created) {
                return badRequestApi("Email already exist");
            }

            // set payload
            String token = "";
            Map<String, String> payload = new HashMap<>();
            payload.put("email", customer.getEmail());
            payload.put("start", customer.getCreateAt().toString());

            // get token
            JWT<Map<String, String>> jwt = new JWT<>();
            token = jwt.createJWT(payload, 3600);

            // get template
            String acceptHtml = mailRender.acceptAccountCustomer(token);

            MailTemplate template = new MailTemplate();
            template.setSubject("Xác thực tài khoản");
            template.setTo(customer.getEmail());
            template.setHtml(acceptHtml);

            // sendEmail
            mailService.sendEmail(template);

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PatchMapping("accept-account")
    ResponseEntity<?> acceptAccount(@RequestBody Map<String, String> data) {
        try {
            // get data
            String token = data.get("token");

            // check token
            if(token.trim() == null) {
                return badRequestApi("Token is null");
            }

            // decode
            JWT<?> jwt = new JWT<>();
            Claims dataToken = jwt.decodeJWT(token);

            // check payload
            if(dataToken == null) {
                return badRequestApi("Invalid token");
            }

            // get map payload
            Map<String, String> payload = Genarate.getMapFromJson(dataToken.getSubject());
            String email = payload.get("email");

            // get customer
            Customer customer = customerService.getCustomerByEmail(email);

            // check customer
            if(customer == null) {
                return badRequestApi("Account not exist");
            }

            if(customer.getAcceptAt() != null) {
                return badRequestApi("Account accepted");
            }

            // set time accept
            customer.setAcceptAt(Genarate.getTimeStamp());

            // update
            Boolean updated = customerService.updateCustomer(customer);

            if(!updated) {
                throw new Exception("Accept error");
            }

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
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

            if(customer.getAcceptAt() == null) {
                return Error(HttpStatus.BAD_REQUEST, "Unverified account");
            }

            if(customer.getBlock() == 1) {
                return Error(HttpStatus.UNAUTHORIZED, "Account has been locked");
            }

            JWT<Customer> jwt = new JWT<>();
            String CToken = jwt.createJWT(customer, Long.parseLong(CustomDotENV.get("TIME_SECOND_TOKEN")));

            Cookie cookie1 = new Cookie("CToken", CToken);
            cookie1.setAttribute("Path", "/customer");
            cookie1.setAttribute("HttpOnly", "True");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("SameSite", "None");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("Partitioned", "True");
            cookie1.setMaxAge(60*6);

            response.addCookie(cookie1);

            Long now  = Genarate.getTimeStamp();
            Long dieToken = now + 6 * 60 * 1000;
            Map<String, String> cookies = new HashMap<>();
            cookies.put("ETok", dieToken + "->MA360");

            ResponseSuccess<Customer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.cookies = cookies;
            responseSuccess.data = customer;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }


    @GetMapping("refresh/tok")
    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, String> data =  (Map<String, String>) request.getAttribute("customerDataToken");

            JWT<Map<String, String>> jwt = new JWT<>();
            String newToken = jwt.createJWT(data, Long.parseLong(CustomDotENV.get("TIME_SECOND_TOKEN")));

            // set cookie
            Cookie cookie1 = new Cookie("CToken", newToken);
            cookie1.setAttribute("Path", "/customer");
            cookie1.setAttribute("HttpOnly", "True");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("SameSite", "None");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("Partitioned", "True");
            cookie1.setMaxAge(60*6);

            response.addCookie(cookie1);

            Long now  = Genarate.getTimeStamp();
            Long dieToken = now + 6 * 60 * 1000;
            Map<String, String> cookies = new HashMap<>();
            cookies.put("ETok", dieToken + "->MA360");

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            responseSuccess.cookies = cookies;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("logout")
    ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("CToken", "");
            cookie.setAttribute("Path", "/customer");
            cookie.setAttribute("HttpOnly", "True");
            cookie.setAttribute("Secure", "True");
            cookie.setAttribute("SameSite", "None");
            cookie.setAttribute("Partitioned", "True");

            cookie.setMaxAge(0);
            response.addCookie(cookie);
            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("authentication")
    ResponseEntity<?> authen() {
        ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
        return ResponseEntity.ok().body(responseSuccess);
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

    @GetMapping("me")
    ResponseEntity<?> acceptForget( HttpServletRequest request) {
        try {
            Map<String, String> customerToken = (Map<String, String>) request.getAttribute("customerDataToken");
            ResponseSuccess<Map<String, String>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = customerToken;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }


    @PatchMapping("change-password")
    ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dataPassword, HttpServletRequest request, HttpServletResponse response) {

        try {
            Map<String, String> customerToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = customerToken.get("uid");

            if(dataPassword.getNewPassword().equals(dataPassword.getOldPassword()) ) {
                return badRequestApi("Password must not same");
            }

            if(!dataPassword.getNewPassword().equals(dataPassword.getConfirmPassword()) ) {
                return badRequestApi("Confirm password not same");
            }

            Customer customer = customerService.getCustomerByUid(uid);

            Hash hash = new Hash();
            if(!customer.getPassword().equals(hash.hash(dataPassword.getOldPassword()))) {
                return badRequestApi("Invalid password");
            }

            String hashNewPassword = hash.hash(dataPassword.getNewPassword());

            customer.setPassword(hashNewPassword);
            customer.setLastLogin(Genarate.getTimeStamp());

            boolean updated = customerService.updateCustomer(customer);

            if(!updated) {
                throw new Exception("Error update");
            }

            JWT<Customer> jwt = new JWT<>();
            String CToken = jwt.createJWT(customer, Long.parseLong(CustomDotENV.get("TIME_SECOND_TOKEN")));

            Map<String, String> cookies = new HashMap<>();
            cookies.put("CToken", CToken);

            Cookie cookie = new Cookie("CToken", CToken);
            cookie.setAttribute("Path", "/customer");
            cookie.setAttribute("HttpOnly", "True");
            cookie.setAttribute("Secure", "True");
            cookie.setAttribute("SameSite", "None");
            cookie.setAttribute("Secure", "True");
            cookie.setAttribute("Partitioned", "True");

            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            ResponseSuccess<Customer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = customer;
            responseSuccess.cookies = cookies;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("change-email")
    ResponseEntity<?> changeEmail (HttpServletRequest request,@Valid @RequestBody ChangeEmailDto dataChange) {
        try {
            Map<String, String> customerToken = (Map<String, String>) request.getAttribute("customerDataToken");

            // lower email
            dataChange.newEmail = dataChange.newEmail.trim().toLowerCase(Locale.ROOT);
            String oldEmail = customerToken.get("email").trim().toLowerCase(Locale.ROOT);
            String lastLogin = customerToken.get("lastLogin");

            // check same email
            if(oldEmail.equals(dataChange.newEmail)) {
                return badRequestApi("Email must not same");
            }

            // check new email exist
            Customer customer = customerService.getCustomerByEmail(dataChange.newEmail);
            if(customer != null) {
                return badRequestApi("Email already exists");
            }


            Map<String, String> payload = new HashMap<>();
            String uid = customerToken.get("uid");
            payload.put("uid", uid);
            payload.put("oldEmail", oldEmail);
            payload.put("lastLogin", lastLogin);
            payload.put("newEmail", dataChange.newEmail);

            // token
            JWT<Map<String, String>> jwt = new JWT<>();
            String tokenChange = jwt.createJWT(payload, 60*10);

            // html
            String htmlTemplate = mailRender.changeEmail(tokenChange, dataChange.newEmail, oldEmail);

            // form email
            MailTemplate template = new MailTemplate();
            template.setSubject("Thay đổi địa chỉ email");
            template.setTo(oldEmail);
            template.setHtml(htmlTemplate);

            // send mail
            mailService.sendEmail(template);

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PatchMapping("accept-change-email")
    ResponseEntity<?> acceptChangeEmail (@Valid @RequestBody AcceptChangeEmailDto data) {
        try {
            String token = data.tokenChange;

            JWT<?> jwt = new JWT<>();
            Claims claimsData = jwt.decodeJWT(token);
            if(claimsData == null) {
                return badRequestApi("Invalid token");
            }

            Map<String, String> mapData = Genarate.getMapFromJson(claimsData.getSubject());

            Customer customer = customerService.getCustomerByUid(mapData.get("uid"));
            if(customer == null) {
                return badRequestApi("Account not exist");
            }

            if(customer.getLastLogin() > Long.parseLong(mapData.get("lastLogin"))) {
                return badRequestApi("Session has ended");
            }

            // change info
            customer.setEmail(mapData.get("newEmail"));
            customer.setLastLogin(Genarate.getTimeStamp());

            Boolean updated = customerService.updateCustomer(customer);
            if(!updated) {
                throw new Exception("Change error");
            }

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }


}
