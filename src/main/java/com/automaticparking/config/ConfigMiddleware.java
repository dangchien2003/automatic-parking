package com.automaticparking.config;

import com.automaticparking.middleware.Admin;
import com.automaticparking.middleware.TokenCustomer;
import com.automaticparking.middleware.TokenStaff;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class ConfigMiddleware implements WebMvcConfigurer {
    private TokenCustomer customer;
    private TokenStaff staff;
    private Admin admin;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // staff token
        registry.addInterceptor(staff)
                .addPathPatterns("/api/staff/create")
                .addPathPatterns("/api/staff/get-all")
                .addPathPatterns("/api/staff/lock/{sid}")
                .addPathPatterns("/api/staff/unlock/{sid}")
                .addPathPatterns("/api/staff/change-password")
                .addPathPatterns("/api/staff/cash/not-approve/get-all")
                .addPathPatterns("/api/staff/cash/approve")
                .addPathPatterns("/api/staff/shop-qr/create");

        // router admin
        registry.addInterceptor(admin)
                .addPathPatterns("/api/staff/create")
                .addPathPatterns("/api/staff/get-all")
                .addPathPatterns("/api/staff/lock/{sid}")
                .addPathPatterns("/api/staff/unlock/{sid}")
                .addPathPatterns("/api/staff/cash/not-approve/get-all")
                .addPathPatterns("/api/staff/shop-qr/create");

        // customer token
        registry.addInterceptor(customer)
                .addPathPatterns("/api/customer/change-password")
                .addPathPatterns("/api/customer/me")
                .addPathPatterns("/api/customer/cash/input-money")
                .addPathPatterns("/api/customer/cash/all")
                .addPathPatterns("/api/customer/cash/remaining")
                .addPathPatterns("/api/customer/code/buy")
                .addPathPatterns("/api/customer/code/bought")
                .addPathPatterns("/api/customer/code/i")
                .addPathPatterns("/api/customer/code/qr/{qrid}")
                .addPathPatterns("/api/customer/change-email")
                .addPathPatterns("/api/customer/code/extend/{qrid}")
                .addPathPatterns("/api/customer/code/extend/price/{qrid}")
                .addPathPatterns("/api/customer/code/cancle")
                .addPathPatterns("/api/customer/bot/i")
                .addPathPatterns("/api/customer/authentication");

    }
}