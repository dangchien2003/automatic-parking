package com.automaticparking.middleware.config;

import com.automaticparking.middleware.Admin;
import com.automaticparking.middleware.TokenStaff;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigMiddleware implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // staff token
        registry.addInterceptor(new TokenStaff())
                .addPathPatterns("/staff/create")
                .addPathPatterns("/staff/get-all")
                .addPathPatterns("/staff/lock/{sid}")
                .addPathPatterns("/staff/unlock/{sid}")
                .addPathPatterns("/staff/update/{sid}")
                .addPathPatterns("/staff/change-password")
                .addPathPatterns("/cash/not-approve/get-all")
                .addPathPatterns("/cash/approve");

        // router admin
        registry.addInterceptor(new Admin())
                .addPathPatterns("/staff/create")
                .addPathPatterns("/staff/get-all")
                .addPathPatterns("/staff/lock/{sid}")
                .addPathPatterns("/staff/unlock/{sid}")
                .addPathPatterns("/staff/update/{sid}")
                .addPathPatterns("/cash/not-approve/get-all");
    }
}