package com.automaticparking.model.customer;


import com.automaticparking.model.customer.dto.RegisterDto;
import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseSuccess;
import encrypt.Hash;
import encrypt.JWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;
import util.Genarate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("customer")
public class CustomerController extends ResponseApi {
    private final CustomerService customerService = new CustomerService();

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
    ResponseEntity<?> login(@Valid @RequestBody RegisterDto dataLogin) {
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
            String utoken = jwt.createJWT(customer);

            Map<String, String> cookies = new HashMap<>();
            cookies.put("UToken", utoken);

            ResponseSuccess<Customer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.cookies = cookies;
            responseSuccess.data = customer;
            return ResponseEntity.ok().body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

}
