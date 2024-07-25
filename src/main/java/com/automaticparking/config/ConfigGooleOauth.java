package com.automaticparking.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@AllArgsConstructor
public class ConfigGooleOauth {
    private Dotenv dotenv;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        String clientId = dotenv.get("CLIENT_ID");
        ;
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

}
