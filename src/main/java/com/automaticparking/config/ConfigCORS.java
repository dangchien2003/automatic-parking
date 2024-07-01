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
public class ConfigCORS implements WebMvcConfigurer{
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://localhost:3000", "http://localhost:3000", "https://fe-parking.onrender.com", "https://feparking.com:3000", "http://feparking.com:3000", "https://child1.feparking.com:3000", "http://child1.feparking.com:3000", "https://bot-app-3wke.onrender.com")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .allowPrivateNetwork(true)
                .maxAge(60);

    }

}
