package com.automaticparking.config;

import com.automaticparking.services.RunService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class ConfigStartupListener {
    private RunService runService;
    private final String active;

    @Autowired
    public ConfigStartupListener(RunService runService, Dotenv dotenv) {
        this.runService = runService;
        this.active = dotenv.get("ACTIVE");
    }

    @EventListener
    public void runCallApi(ContextRefreshedEvent event) {
        if (!active.isEmpty() && active.equals("prod")) {
            runService.run();
        } else {
            System.out.println("ACTIVE: " + active);
        }
    }
}
