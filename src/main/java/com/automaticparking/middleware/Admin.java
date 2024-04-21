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
public class Admin extends ResponseApi implements HandlerInterceptor  {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("staffDataToken");
        if(staffDataToken.get("admin").equals("1")) {
            return true;
        }
        ResponseEntity<ResponseEntity> errorResponse = new ResponseEntity<>(Error(HttpStatus.UNAUTHORIZED, "Not have access"), HttpStatus.UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse.getBody().getBody());
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
        response.setStatus(errorResponse.getStatusCodeValue());
        return false; // endpoint
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {}
}
