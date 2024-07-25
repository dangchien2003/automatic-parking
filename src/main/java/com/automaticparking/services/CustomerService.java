package com.automaticparking.services;

import com.automaticparking.Repositorys.CustomerRepository;
import com.automaticparking.database.dto.*;
import com.automaticparking.database.entity.Customer;
import com.automaticparking.encrypt.Hash;
import com.automaticparking.exception.*;
import com.automaticparking.types.ResponseSuccess;
import com.automaticparking.util.*;
import com.automaticparking.validation.EmailValid;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CustomerService {
    private final MailService mailService;
    private final RenderService mailRender;
    private CustomerRepository customerRepository;
    private CacheService cacheService;
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    private Dotenv dotenv;
    private JWT jwt;

    public ResponseEntity<ResponseSuccess> createAccount(RegisterDto registerDto) {
        Long now = Generate.getTimeStamp();
        Hash hash = new Hash();
        Customer customer = getCustomer(registerDto.getEmail());
        if (customer != null) {
            if (customer.getAcceptAt() != null) {
                throw new ConflictException("Email already exist");
            }

            customer.setPassword(hash.hash((registerDto.getPassword())));
            customerRepository.save(customer);
        } else {
            customer = new Customer(Generate.generateId("CUSTOMER_", 3), registerDto.getEmail(), hash.hash(registerDto.getPassword()), now, now, 0);
            customerRepository.save(customer);
        }

        // set payload
        String token = "";
        Map<String, String> payload = new HashMap<>();
        payload.put("email", customer.getEmail());
        payload.put("start", customer.getCreateAt().toString());

        // get token

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

        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    public ResponseEntity<ResponseSuccess> acceptAccount(Map<String, String> data) {
        // get data
        String token = data.get("token");

        // check token
        if (token == null || token.trim() == null) {
            throw new BadRequestException("Token is null");
        }

        // decode

        Claims dataToken = jwt.decodeJWT(token);

        // check payload
        if (dataToken == null) {
            throw new BadRequestException("Invalid accept token");
        }

        // get map payload
        Map<String, String> payload = Generate.getMapFromJson(dataToken.getSubject());
        String email = payload.get("email");

        Customer customer = getCustomer(email);

        // check customer
        if (customer == null) {
            throw new BadRequestException("Account not exist");
        }

        if (customer.getAcceptAt() != null) {
            throw new BadRequestException("Account accepted");
        }

        // set time accept
        customer.setAcceptAt(Generate.getTimeStamp());

        // update
        customerRepository.save(customer);

        // set cache
        setCustomerToCache(customer, customer.getUid());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    public ResponseEntity<ResponseSuccess> login(LoginDto dataLogin, HttpServletResponse response) {
        Customer customer = customerRepository.findCustomerByEmail(dataLogin.getEmail()).orElseThrow(() -> new NotFoundException());

        if (customer == null) {
            throw new NotFoundException("Email not exist");
        }

        Hash hash = new Hash();
        if (!hash.compareHash(dataLogin.getPassword(), customer.getPassword())) {
            throw new InvalidException("Incorrect password");
        }

        if (customer.getAcceptAt() == null) {
            throw new AuthorizedException("Unverified account");
        }

        if (customer.getBlock() == 1) {
            throw new AuthorizedException("Account has been locked");
        }

        int age = Integer.parseInt(dotenv.get("TIME_SECOND_TOKEN"));
        AccessToken data = getDataAuthen(customer, age);

        setCustomerToCache(customer, customer.getUid());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(data, status), status);
    }

    private AccessToken getDataAuthen(Customer customer, int age) {

        String CToken = jwt.createJWT(customer, age);

        return new AccessToken(CToken, "Bearer", age);
    }

    public ResponseEntity<ResponseSuccess> loginGoogle(String googleToken, HttpServletResponse response) {
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(googleToken);
        } catch (Exception e) {
            idToken = null;
        }
        if (idToken == null) {
            throw new AuthorizedException("Invalid google token");
        }
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String pictureUrl = (String) payload.get("picture");

        Customer customer = getCustomer(email);
        if (customer != null) {
            if (customer.getAcceptAt() == null) {
                throw new BadRequestException("Unverified account");
            }

            if (customer.getBlock() == 1) {
                throw new AuthorizedException("Account has been locked");
            }
        } else {
            // create not yet
            Hash hash = new Hash();
            long now = Generate.getTimeStamp();
            customer = new Customer(Generate.generateId("CUSTOMER_", 3), email, hash.hash(Random.generateRandomString(15)), now, now, 0);
            customer.setAcceptAt(now);
            customerRepository.save(customer);
        }

        int age = Integer.parseInt(dotenv.get("TIME_SECOND_TOKEN"));
        AccessToken data = getDataAuthen(customer, age);

        // setCache
        setCustomerToCache(customer, customer.getUid());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(data, status), status);
    }


    public ResponseEntity<ResponseSuccess> refresh(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = Author.getAuthor(authorizationHeader);


        Claims payload = jwt.decodeJWT(token);

        if (payload == null) {
            throw new AuthorizedException("Invalid token");
        }

        Json<Customer> json = new Json<>();
        Customer customer = json.jsonParse(payload.getSubject(), Customer.class);

        int age = Integer.parseInt(dotenv.get("TIME_SECOND_TOKEN"));
        AccessToken data = getDataAuthen(customer, age);

        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(data, status), status);
    }

    public ResponseEntity<ResponseSuccess> logout(HttpServletResponse response) {
        String[] deleteCookies = {"nextRefresh"};
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(deleteCookies, status), status);
    }

    public ResponseEntity<ResponseSuccess> forget(@Valid @RequestBody ForgetPassword forgetPassword) {
        Customer customer = getCustomer(forgetPassword.email);

        if (customer == null) {
            throw new BadRequestException("Email not exist");
        }

        // set data token
        forgetPassword.lastLogin = customer.getLastLogin();


        String forgetToken = jwt.createJWT(forgetPassword, Long.parseLong(Objects.requireNonNull(dotenv.get("FORGET_PASSWORD_SECOND_TOKEN"))));

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
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    public ResponseEntity<ResponseSuccess> acceptForget(String forgetToken) {

        Claims dataToken = jwt.decodeJWT(forgetToken);

        if (dataToken == null) {
            throw new BadRequestException("Invalid accept token");
        }

        Map<String, String> dataForget = Generate.getMapFromJson(dataToken.getSubject());

        String email = dataForget.get("email");

        if (email.trim().isEmpty()) {
            throw new BadRequestException("Invalid email");
        }

        Customer customer = getCustomer(email);

        if (customer == null) {
            throw new BadRequestException("Email not exist");
        }

        if (dataForget.get("lastLogin") == null || customer.getLastLogin() != Long.parseLong(dataForget.get("lastLogin"))) {
            throw new BadRequestException("Token has expired");
        }

        String newPassword = Random.generateRandomString(10);

        Hash hash = new Hash();
        String hashNewPassword = hash.hash(newPassword);

        customer.setPassword(hashNewPassword);
        customer.setLastLogin(Generate.getTimeStamp());
        customerRepository.save(customer);

        String html = mailRender.customerNewPassword(newPassword);

        MailTemplate mailTemplate = new MailTemplate();
        mailTemplate.setSubject("Cập nhật mật khẩu");
        mailTemplate.setHtml(html);
        mailTemplate.setTo(email);

        mailService.sendEmail(mailTemplate);

        // set cache
        setCustomerToCache(customer, customer.getUid());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    public ResponseEntity<ResponseSuccess> getMyinfo(HttpServletRequest request) {
        Customer customerToken = (Customer) request.getAttribute("customerDataToken");
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(customerToken, status), status);
    }


    public ResponseEntity<ResponseSuccess> changePassword(ChangePasswordDto dataPassword, HttpServletRequest request, HttpServletResponse response) {
        Customer customerToken = (Customer) request.getAttribute("customerDataToken");
        String uid = customerToken.getUid();

        if (dataPassword.getNewPassword().equals(dataPassword.getOldPassword())) {
            throw new BadRequestException("Password must not same");
        }

        if (!dataPassword.getNewPassword().equals(dataPassword.getConfirmPassword())) {
            throw new BadRequestException("Confirm password not same");
        }

        Customer customer = getCustomer(uid);

        Hash hash = new Hash();
        if (!customer.getPassword().equals(hash.hash(dataPassword.getOldPassword()))) {
            throw new BadRequestException("Invalid password");
        }

        String hashNewPassword = hash.hash(dataPassword.getNewPassword());

        customer.setPassword(hashNewPassword);
        customer.setLastLogin(Generate.getTimeStamp());

        customerRepository.save(customer);

        int age = Integer.parseInt(dotenv.get("TIME_SECOND_TOKEN"));
        AccessToken data = getDataAuthen(customer, age);

        // set cache
        setCustomerToCache(customer, customer.getUid());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(data, status), status);
    }

    public ResponseEntity<ResponseSuccess> changeEmail(HttpServletRequest request, ChangeEmailDto dataChange) {
        Customer customerToken = (Customer) request.getAttribute("customerDataToken");

        // lower email
        dataChange.setNewEmail(dataChange.getNewEmail().trim().toLowerCase(Locale.ROOT));
        String oldEmail = customerToken.getEmail().trim().toLowerCase(Locale.ROOT);
        Long lastLogin = customerToken.getLastLogin();

        // check same email
        if (oldEmail.equals(dataChange.getNewEmail())) {
            throw new BadRequestException("Email must not same");
        }

        // check new email exist
        Customer customer = getCustomer(dataChange.getNewEmail());
        if (customer != null) {
            throw new BadRequestException("Email already exists");
        }


        Map<String, String> payload = new HashMap<>();
        String uid = customerToken.getUid();
        payload.put("uid", uid);
        payload.put("oldEmail", oldEmail);
        payload.put("lastLogin", lastLogin.toString());
        payload.put("newEmail", dataChange.getNewEmail());
        // token

        String tokenChange = jwt.createJWT(payload, 60 * 10);

        // html
        String htmlTemplate = mailRender.changeEmail(tokenChange, dataChange.getNewEmail(), oldEmail);

        // form email
        MailTemplate template = new MailTemplate();
        template.setSubject("Thay đổi địa chỉ email");
        template.setTo(oldEmail);
        template.setHtml(htmlTemplate);

        // send mail
        mailService.sendEmail(template);

        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    public ResponseEntity<ResponseSuccess> acceptChangeEmail(AcceptChangeEmailDto data) {
        String token = data.tokenChange;


        Claims claimsData = jwt.decodeJWT(token);
        if (claimsData == null) {
            throw new BadRequestException("Invalid change token");
        }

        Map<String, String> mapData = Generate.getMapFromJson(claimsData.getSubject());

        Customer customer = getCustomer(mapData.get("uid"));
        if (customer == null) {
            throw new BadRequestException("Account not exist");
        }

        if (customer.getLastLogin() > Long.parseLong(mapData.get("lastLogin"))) {
            throw new BadRequestException("Session has ended");
        }

        Customer customerNewEmail = customerRepository.findCustomerByEmail(mapData.get("newEmail")).orElse(null);
        if (customerNewEmail != null) {
            throw new ConflictException("Email already exist");
        }
        // change info
        customer.setEmail(mapData.get("newEmail"));
        customer.setLastLogin(Generate.getTimeStamp());

        customerRepository.save(customer);

        // set cache
        setCustomerToCache(customer, customer.getUid());

        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
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
                customer = customerRepository.findCustomerByEmail(key).orElseThrow(() -> new NotFoundException());
            } else {
                customer = customerRepository.findById(key).orElseThrow(() -> new NotFoundException());
            }
            setCustomerToCache(customer, key);
        }
        return customer;
    }

}