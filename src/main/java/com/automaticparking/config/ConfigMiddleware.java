package com.automaticparking.config;

import com.automaticparking.middleware.Admin;
import com.automaticparking.middleware.TokenCustomer;
import com.automaticparking.middleware.TokenStaff;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigMiddleware implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        TokenStaff staff = new TokenStaff();
        Admin admin = new Admin();
        TokenCustomer customer = new TokenCustomer();

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
                .addPathPatterns("/customer/cash/input-money")
                .addPathPatterns("/customer/cash/all")
                .addPathPatterns("/customer/cash/remaining")
                .addPathPatterns("/customer/code/buy");

    }
}