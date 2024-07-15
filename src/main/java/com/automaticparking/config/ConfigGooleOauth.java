package com.automaticparking.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.DotENV;

import java.util.Collections;

@Configuration
public class ConfigGooleOauth {
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        String clientId = DotENV.get("CLIENT_ID");
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

}
