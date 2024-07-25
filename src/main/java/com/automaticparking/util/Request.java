package com.automaticparking.util;

import jakarta.servlet.http.HttpServletRequest;

public class Request {
    public static boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                && request.getHeader("Origin") != null
                && request.getHeader("Access-Control-Request-Method") != null;
    }
}
