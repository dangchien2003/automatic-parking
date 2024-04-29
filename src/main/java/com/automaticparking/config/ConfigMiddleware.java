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
        registry.addInterceptor(staff).addPathPatterns("/staff/create");
        registry.addInterceptor(staff).addPathPatterns("/staff/get-all");
        registry.addInterceptor(staff).addPathPatterns("/staff/lock/{sid}");
        registry.addInterceptor(staff).addPathPatterns("/staff/unlock/{sid}");
        registry.addInterceptor(staff).addPathPatterns("/staff/change-password");
        registry.addInterceptor(staff).addPathPatterns("/staff/cash/not-approve/get-all");
        registry.addInterceptor(staff).addPathPatterns("/staff/cash/approve");
        registry.addInterceptor(staff).addPathPatterns("/staff/shop-qr/create");

        // router admin
        registry.addInterceptor(admin).addPathPatterns("/staff/create");
        registry.addInterceptor(admin).addPathPatterns("/staff/get-all");
        registry.addInterceptor(admin).addPathPatterns("/staff/lock/{sid}");
        registry.addInterceptor(admin).addPathPatterns("/staff/unlock/{sid}");
        registry.addInterceptor(admin).addPathPatterns("/staff/cash/not-approve/get-all");
        registry.addInterceptor(admin).addPathPatterns("/staff/shop-qr/create");

        // customer token
        registry.addInterceptor(customer).addPathPatterns("/customer/cash/input-money");
        registry.addInterceptor(customer).addPathPatterns("/customer/cash/all");
        registry.addInterceptor(customer).addPathPatterns("/customer/code/buy");

    }
}