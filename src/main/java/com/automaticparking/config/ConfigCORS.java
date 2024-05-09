package com.automaticparking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class ConfigCORS implements WebMvcConfigurer{
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("https://localhost:3000", "http://localhost:3000", "https://fe-parking.onrender.com", "https://feparking.com:3000", "http://feparking.com:3000", "https://child1.feparking.com:3000", "http://child1.feparking.com:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .allowPrivateNetwork(true)
                .maxAge(60);

    }

}
