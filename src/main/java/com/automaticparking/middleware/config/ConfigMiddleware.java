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
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/create");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/get-all");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/lock/{sid}");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/unlock/{sid}");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/change-password");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/cash/not-approve/get-all");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/cash/approve");


        // router admin
        registry.addInterceptor(new Admin()).addPathPatterns("/staff/create");
        registry.addInterceptor(new Admin()).addPathPatterns("/staff/get-all");
        registry.addInterceptor(new Admin()).addPathPatterns("/staff/lock/{sid}");
        registry.addInterceptor(new Admin()).addPathPatterns("/staff/unlock/{sid}");
        registry.addInterceptor(new Admin()).addPathPatterns("/cash/not-approve/get-all");
    }
}