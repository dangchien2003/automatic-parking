package com.automaticparking.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.CustomDotENV;

@Configuration
public class ConfigCloudinary {

    @Bean
    public Cloudinary cloudinary() {
        System.out.println("Cloudinary OK");
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CustomDotENV.get("CLOUD_NAME"),
                "api_key", CustomDotENV.get("API_KEY"),
                "api_secret", CustomDotENV.get("API_SECRET")
        ));
    }
}