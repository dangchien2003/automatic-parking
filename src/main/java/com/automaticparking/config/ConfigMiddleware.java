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
                .addPathPatterns("/staff/create")
                .addPathPatterns("/staff/get-all")
                .addPathPatterns("/staff/lock/{sid}")
                .addPathPatterns("/staff/unlock/{sid}")
                .addPathPatterns("/staff/change-password")
                .addPathPatterns("/staff/cash/not-approve/get-all")
                .addPathPatterns("/staff/cash/approve")
                .addPathPatterns("/staff/shop-qr/create");

        // router admin
        registry.addInterceptor(admin)
                .addPathPatterns("/staff/create")
                .addPathPatterns("/staff/get-all")
                .addPathPatterns("/staff/lock/{sid}")
                .addPathPatterns("/staff/unlock/{sid}")
                .addPathPatterns("/staff/cash/not-approve/get-all")
                .addPathPatterns("/staff/shop-qr/create");

        // customer token
        registry.addInterceptor(customer)
                .addPathPatterns("/customer/change-password")
                .addPathPatterns("/customer/me")
                .addPathPatterns("/customer/cash/input-money")
                .addPathPatterns("/customer/cash/all")
                .addPathPatterns("/customer/cash/remaining")
                .addPathPatterns("/customer/code/buy")
                .addPathPatterns("/customer/code/bought")
                .addPathPatterns("/customer/code/i")
                .addPathPatterns("/customer/code/qr/{qrid}")
                .addPathPatterns("/customer/change-email")
                .addPathPatterns("/customer/code/extend/{qrid}")
                .addPathPatterns("/customer/code/extend/price/{qrid}")
                .addPathPatterns("/customer/code/cancle")
                .addPathPatterns("/customer/bot/i")
                .addPathPatterns("/customer/authentication");

    }
}