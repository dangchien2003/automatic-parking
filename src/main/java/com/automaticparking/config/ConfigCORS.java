package com.automaticparking.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class ConfigCORS implements WebMvcConfigurer {
    @Autowired
    private Dotenv dotenv;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(String.format("https://autoparking-bot-%s.onrender.com", dotenv.get("VERSION_TEST")),
                        String.format("https://autoparking-%s.onrender.com", dotenv.get("VERSION_TEST")),
                        String.format("https://autoparking-bot-%s.onrender.com", dotenv.get("VERSION_PROD")),
                        String.format("https://autoparking-%s.onrender.com", dotenv.get("VERSION_PROD")),
                        "https://localhost:3000",
                        "http://localhost:3000",
                        "https://localhost:3001",
                        "http://localhost:3001")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .allowPrivateNetwork(true)
                .maxAge(60);
    }

}
