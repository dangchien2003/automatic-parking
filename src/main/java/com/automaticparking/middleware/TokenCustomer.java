package com.automaticparking.middleware;

import com.automaticparking.database.entity.Customer;
import com.automaticparking.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import encrypt.JWT;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import util.Cookies;
import response.ResponseApi;
import util.Json;

@Component
public class TokenCustomer extends ResponseApi implements HandlerInterceptor {
    private CustomerService customerService;

    @Autowired
    public TokenCustomer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Cookie[] cookies = request.getCookies();
        Cookies CookieUtil = new Cookies(cookies);
        Cookie Ctoken = CookieUtil.getCookieByName("CToken");

        if (Ctoken == null) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Not found token"), HttpStatus.OK);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false; // endpoint
        }

        String token = Ctoken.getValue();
        Boolean error = false;
        if (token.trim() == "") {
            error = true;
        }

        Customer customerDataToken = null;

        if (!error) {
            JWT<Customer> jwt = new JWT<>();
            Claims dataToken = jwt.decodeJWT(token);
            if (dataToken == null) {
                error = true;
            } else {
                Json<Customer> json = new Json<>();
                //  lấy dữ liệu token
                customerDataToken = json.jsonParse(dataToken.getSubject(), Customer.class);
            }
        }

        if (error) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Invalid token"), HttpStatus.BAD_REQUEST);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false; // endpoint
        }

        String uid = customerDataToken.getUid();
        Customer customerInfo = customerService.getCustomer(uid);
        // kiểm tra tài khoản bị block
        if (customerInfo.getBlock() == 1) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Account Blocked"), HttpStatus.UNAUTHORIZED);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false;
        }

        // kiểm tra phiên đăng nhập của tk
        if (!customerInfo.getLastLogin().equals(customerDataToken.getLastLogin())) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Login session ended"), HttpStatus.BAD_REQUEST);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false;
        }

        request.setAttribute("customerDataToken", customerDataToken);
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {
    }
}
