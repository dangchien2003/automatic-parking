package com.automaticparking.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.DotENV;

@Configuration
public class ConfigCloudinary {

    @Bean
    public Cloudinary cloudinary() {
        System.out.println("Cloudinary OK");
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", DotENV.get("CLOUD_NAME"),
                "api_key", DotENV.get("API_KEY"),
                "api_secret", DotENV.get("API_SECRET")
        ));
    }
}