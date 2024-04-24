package com.automaticparking.model.customer;


import com.automaticparking.model.customer.dto.RegisterDto;
import com.automaticparking.types.ResponseSuccess;
import encrypt.Hash;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import javax.validation.Valid;

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
}
