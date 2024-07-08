package com.automaticparking.model.customer;


import com.automaticparking.model.cache.CacheService;
import com.automaticparking.model.customer.dto.*;
import com.automaticparking.model.mailer.MailService;
import com.automaticparking.model.mailer.MailTemplate;
import com.automaticparking.model.mailer.Render;
import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseSuccess;
import encrypt.Hash;
import encrypt.JWT;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;
import util.DotENV;
import util.Genarate;
import util.Random;
import validation.EmailValid;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class CustomerService extends ResponseApi {
    private final MailService mailService;

    private final Render mailRender;
    private CustomerRepository customerRepository;

    private CacheService cacheService;

    @Autowired
    public CustomerService(MailService mailService, Render mailRender, CustomerRepository customerRepository, CacheService cacheService) {
        this.mailService = mailService;
        this.mailRender = mailRender;
        this.customerRepository = customerRepository;
        this.cacheService = cacheService;
    }

    ResponseEntity<?> createAdmin(RegisterDto registerDto) {
        try {
            Long now = Genarate.getTimeStamp();
            Hash hash = new Hash();

            Customer customer = new Customer(Util.genarateUid(), registerDto.email, hash.hash(registerDto.password), now, now, 0);
            Boolean created = customerRepository.saveCustomer(customer);

            if (!created) {
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

            // set cache
            setCustomerToCache(customer, customer.getUid());

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> acceptAccount(Map<String, String> data) {
        try {
            // get data
            String token = data.get("token");

            // check token
            if (token.trim() == null) {
                return badRequestApi("Token is null");
            }

            // decode
            JWT<?> jwt = new JWT<>();
            Claims dataToken = jwt.decodeJWT(token);

            // check payload
            if (dataToken == null) {
                return badRequestApi("Invalid token");
            }

            // get map payload
            Map<String, String> payload = Genarate.getMapFromJson(dataToken.getSubject());
            String uid = payload.get("uid");

            Customer customer = getCustomer(uid);

            // check customer
            if (customer == null) {
                return badRequestApi("Account not exist");
            }

            if (customer.getAcceptAt() != null) {
                return badRequestApi("Account accepted");
            }

            // set time accept
            customer.setAcceptAt(Genarate.getTimeStamp());

            // update
            Boolean updated = customerRepository.updateCustomer(customer);

            if (!updated) {
                throw new Exception("Accept error");
            }

            // set cache
            setCustomerToCache(customer, customer.getUid());

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> login(RegisterDto dataLogin, HttpServletResponse response) {
        try {
            Customer customer = customerRepository.getCustomerByEmail(dataLogin.email);

            if (customer == null) {
                return badRequestApi("Email not exist");
            }

            Hash hash = new Hash();
            if (!hash.compareHash(dataLogin.password, customer.getPassword())) {
                return badRequestApi("Incorrect password");
            }

            if (customer.getAcceptAt() == null) {
                return Error(HttpStatus.BAD_REQUEST, "Unverified account");
            }

            if (customer.getBlock() == 1) {
                return Error(HttpStatus.UNAUTHORIZED, "Account has been locked");
            }

            JWT<Customer> jwt = new JWT<>();
            String CToken = jwt.createJWT(customer, Long.parseLong(DotENV.get("TIME_SECOND_TOKEN")));

            Cookie cookie1 = new Cookie("CToken", CToken);
            cookie1.setAttribute("Path", "/customer");
            cookie1.setAttribute("HttpOnly", "True");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("SameSite", "None");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("Partitioned", "True");
            cookie1.setMaxAge(60 * 6);

            response.addCookie(cookie1);

            Long now = Genarate.getTimeStamp();
            Long dieToken = now + 6 * 60 * 1000;
            Map<String, String> cookies = new HashMap<>();
            cookies.put("ETok", dieToken + "->MA360");

            // setCache
            setCustomerToCache(customer, customer.getUid());

            ResponseSuccess<Customer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.cookies = cookies;
            responseSuccess.data = customer;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, String> data = (Map<String, String>) request.getAttribute("customerDataToken");

            JWT<Map<String, String>> jwt = new JWT<>();
            String newToken = jwt.createJWT(data, Long.parseLong(DotENV.get("TIME_SECOND_TOKEN")));

            // set cookie
            Cookie cookie1 = new Cookie("CToken", newToken);
            cookie1.setAttribute("Path", "/customer");
            cookie1.setAttribute("HttpOnly", "True");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("SameSite", "None");
            cookie1.setAttribute("Secure", "True");
            cookie1.setAttribute("Partitioned", "True");
            cookie1.setMaxAge(60 * 6);

            response.addCookie(cookie1);

            Long now = Genarate.getTimeStamp();
            Long dieToken = now + 6 * 60 * 1000;
            Map<String, String> cookies = new HashMap<>();
            cookies.put("ETok", dieToken + "->MA360");

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            responseSuccess.cookies = cookies;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

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
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> authen() {
        ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
        return ResponseEntity.ok().body(responseSuccess);
    }

    ResponseEntity<?> forget(@Valid @RequestBody ForgetPassword forgetPassword) {
        try {
            Customer customer = getCustomer(forgetPassword.email);

            if (customer == null) {
                return badRequestApi("Email not exist");
            }

            // set data token
            forgetPassword.lastLogin = customer.getLastLogin();

            JWT<ForgetPassword> jwt = new JWT<>();
            String forgetToken = jwt.createJWT(forgetPassword, Long.parseLong(DotENV.get("FORGET_PASSWORD_SECOND_TOKEN")));

            // get template
            String html = mailRender.customerForget(forgetToken);

            // set conten mail
            MailTemplate mailTemplate = new MailTemplate();
            mailTemplate.setSubject("Quên mật khẩu");
            mailTemplate.setTo(forgetPassword.email);
            mailTemplate.setHtml(html);


            // set cache
            cacheService.setCache("info_" + customer.getEmail(), customer);
            mailService.sendEmail(mailTemplate);
            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> acceptForget(String forgetToken) {
        try {
            JWT<ForgetPassword> jwt = new JWT<>();
            Claims dataToken = jwt.decodeJWT(forgetToken);

            if (dataToken == null) {
                return badRequestApi("Invalid token");
            }

            Map<String, String> dataForget = Genarate.getMapFromJson(dataToken.getSubject());

            String email = dataForget.get("email");

            if (email.trim().isEmpty()) {
                return badRequestApi("Invalid email");
            }

            Customer customer = getCustomer(email);

            if (customer == null) {
                return badRequestApi("Email not exist");
            }

            if (dataForget.get("lastLogin") == null || customer.getLastLogin() != Long.parseLong(dataForget.get("lastLogin"))) {
                return badRequestApi("Token has expired");
            }

            String newPassword = Random.generateRandomString(10);

            Hash hash = new Hash();
            String hashNewPassword = hash.hash(newPassword);

            customer.setPassword(hashNewPassword);
            customer.setLastLogin(Genarate.getTimeStamp());
            Boolean updated = customerRepository.updateCustomer(customer);
            if (!updated) {
                throw new Exception("Error update");
            }

            String html = mailRender.customerNewPassword(newPassword);

            MailTemplate mailTemplate = new MailTemplate();
            mailTemplate.setSubject("Cập nhật mật khẩu");
            mailTemplate.setHtml(html);
            mailTemplate.setTo(email);

            mailService.sendEmail(mailTemplate);

            // set cache
            setCustomerToCache(customer, customer.getUid());

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> getMyinfo(HttpServletRequest request) {
        try {
            Map<String, String> customerToken = (Map<String, String>) request.getAttribute("customerDataToken");
            ResponseSuccess<Map<String, String>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = customerToken;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }


    ResponseEntity<?> changePassword(ChangePasswordDto dataPassword, HttpServletRequest request, HttpServletResponse response) {

        try {
            Map<String, String> customerToken = (Map<String, String>) request.getAttribute("customerDataToken");
            String uid = customerToken.get("uid");

            if (dataPassword.getNewPassword().equals(dataPassword.getOldPassword())) {
                return badRequestApi("Password must not same");
            }

            if (!dataPassword.getNewPassword().equals(dataPassword.getConfirmPassword())) {
                return badRequestApi("Confirm password not same");
            }

            Customer customer = getCustomer(uid);

            Hash hash = new Hash();
            if (!customer.getPassword().equals(hash.hash(dataPassword.getOldPassword()))) {
                return badRequestApi("Invalid password");
            }

            String hashNewPassword = hash.hash(dataPassword.getNewPassword());

            customer.setPassword(hashNewPassword);
            customer.setLastLogin(Genarate.getTimeStamp());

            boolean updated = customerRepository.updateCustomer(customer);

            if (!updated) {
                throw new Exception("Error update");
            }

            JWT<Customer> jwt = new JWT<>();
            String CToken = jwt.createJWT(customer, Long.parseLong(DotENV.get("TIME_SECOND_TOKEN")));

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

            // set cache
            setCustomerToCache(customer, customer.getUid());

            ResponseSuccess<Customer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = customer;
            responseSuccess.cookies = cookies;
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> changeEmail(HttpServletRequest request, ChangeEmailDto dataChange) {
        try {
            Map<String, String> customerToken = (Map<String, String>) request.getAttribute("customerDataToken");

            // lower email
            dataChange.newEmail = dataChange.newEmail.trim().toLowerCase(Locale.ROOT);
            String oldEmail = customerToken.get("email").trim().toLowerCase(Locale.ROOT);
            String lastLogin = customerToken.get("lastLogin");

            // check same email
            if (oldEmail.equals(dataChange.newEmail)) {
                return badRequestApi("Email must not same");
            }

            // check new email exist
            Customer customer = getCustomer(dataChange.newEmail);
            if (customer != null) {
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
            String tokenChange = jwt.createJWT(payload, 60 * 10);

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
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> acceptChangeEmail(AcceptChangeEmailDto data) {
        try {
            String token = data.tokenChange;

            JWT<?> jwt = new JWT<>();
            Claims claimsData = jwt.decodeJWT(token);
            if (claimsData == null) {
                return badRequestApi("Invalid token");
            }

            Map<String, String> mapData = Genarate.getMapFromJson(claimsData.getSubject());

            Customer customer = getCustomer(mapData.get("uid"));
            if (customer == null) {
                return badRequestApi("Account not exist");
            }

            if (customer.getLastLogin() > Long.parseLong(mapData.get("lastLogin"))) {
                return badRequestApi("Session has ended");
            }

            // change info
            customer.setEmail(mapData.get("newEmail"));
            customer.setLastLogin(Genarate.getTimeStamp());

            Boolean updated = customerRepository.updateCustomer(customer);
            if (!updated) {
                throw new Exception("Change error");
            }

            // set cache
            setCustomerToCache(customer, customer.getUid());

            ResponseSuccess<?> responseSuccess = new ResponseSuccess<>();
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public Customer getCustomerFromCache(String key) {
        Customer customer = cacheService.getCache("info_" + key);
        return customer;
    }

    public void setCustomerToCache(Customer customer, String key) {
        cacheService.setCache("info_" + key, customer);
    }

    public Customer getCustomer(String key) {
        Customer customer = getCustomerFromCache(key);
        if (customer == null) {
            if (EmailValid.IsEmail(key)) {
                customer = customerRepository.getCustomerByEmail(key);
            } else {
                customer = customerRepository.getCustomerByUid(key);
            }
            setCustomerToCache(customer, key);
        }
        return customer;
    }
}
