package com.automaticparking.middleware;

import com.automaticparking.util.JWT;
import com.automaticparking.database.entity.Customer;
import com.automaticparking.exception.AuthorizedException;
import com.automaticparking.exception.BaseError;
import com.automaticparking.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.automaticparking.util.Author;
import com.automaticparking.util.Json;
import com.automaticparking.util.Request;

@Component
@AllArgsConstructor
public class TokenCustomer extends BaseError implements HandlerInterceptor {
    private CustomerService customerService;
    private JWT jwt;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (Request.isPreflightRequest(request)) {
            return true;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String token = Author.getAuthor(authorizationHeader);

        if (token == null || token.isEmpty()) {
            ResponseEntity<BaseError> errorResponse = new ResponseEntity<>(setError(HttpStatus.UNAUTHORIZED, "Invalid token"), HttpStatus.UNAUTHORIZED);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody());

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false; // endpoint
        }

        Boolean error = false;
        if (token.trim() == "") {
            error = true;
        }

        Customer customerDataToken = null;

        if (!error) {
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
            throw new AuthorizedException("Invalid token");
        }

        String uid = customerDataToken.getUid();
        Customer customerInfo = customerService.getCustomer(uid);
        // kiểm tra tài khoản bị block
        if (customerInfo.getBlock() == 1) {
            throw new AuthorizedException("Account Blocked");
        }

        // kiểm tra phiên đăng nhập của tk
        if (!customerInfo.getLastLogin().equals(customerDataToken.getLastLogin())) {
            throw new AuthorizedException("Login session ended");
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
