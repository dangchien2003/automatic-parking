package com.automaticparking.middleware.config;

import com.automaticparking.middleware.TokenStaff;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfigMiddleware implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/create");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/get-all");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/lock/{sid}");
        registry.addInterceptor(new TokenStaff()).addPathPatterns("/staff/unlock/{sid}");



    }
}