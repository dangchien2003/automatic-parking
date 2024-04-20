package com.automaticparking.middleware;

import com.automaticparking.model.staff.Staff;
import com.automaticparking.model.staff.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import encrypt.JWT;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import util.Cookies;
import util.Genarate;
import util.ResponseApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TokenStaff extends ResponseApi implements HandlerInterceptor  {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Cookie[] cookies = request.getCookies();
        Cookies CookieUtil = new Cookies(cookies);
        Cookie Stoken = CookieUtil.getCookieByName("SToken");
        if(Stoken == null) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Not found token"), HttpStatus.BAD_REQUEST);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false; // endpoint
        }

        String token = Stoken.getValue();
        Boolean error = false;
        if(token.trim() == "") {
            error = true;
        }

        Map<String, String> staffDataToken = null;

        if(!error) {
            JWT<Staff> jwt = new JWT<>();
            Claims dataToken = jwt.decodeJWT(token);
            if(dataToken == null) {
                error = true;
            }else {
                //  lấy dữ liệu token
                staffDataToken = Genarate.getMapFromJson(dataToken.getSubject());
            }
        }

        if(error) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Invalid token"), HttpStatus.BAD_REQUEST);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false; // endpoint
        }

        StaffService staffService = new StaffService();
        Staff staffInfo = staffService.getOneStaffByEmail(staffDataToken.get("email"));

        // kiểm tra tài khoản bị block
        if(staffInfo.getBlock() == 1) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Account Blocked"), HttpStatus.UNAUTHORIZED);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false; // endpoint
        }

        // kiểm tra phiên đăng nhập của tk
        if(staffInfo.getLastLogin() != Long.parseLong(staffDataToken.get("lastLogin"))) {
            ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(badRequestApi("Login session ended"), HttpStatus.BAD_REQUEST);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.setStatus(errorResponse.getStatusCodeValue());
            return false; // endpoint
        }

        request.setAttribute("staffDataToken", staffDataToken);
        System.out.println( request.getAttribute("staffDataToken"));
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {}
}
