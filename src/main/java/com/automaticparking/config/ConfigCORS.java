package com.automaticparking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sound.midi.Patch;

@Configuration
@EnableWebMvc
public class ConfigCORS implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://autoparking-readplate-v2.onrender.com", "https://autoparking-v2.onrender.com", "https://autoparking-readplate-v1.onrender.com", "https://autoparking-v1.onrender.com", "https://localhost:3000", "http://localhost:3000", "https://localhost:3001", "http://localhost:3001")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .allowPrivateNetwork(true)
                .maxAge(60);

    }

}
