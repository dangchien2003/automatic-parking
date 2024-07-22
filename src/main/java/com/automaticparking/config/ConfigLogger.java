package com.automaticparking.config;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigLogger {
    @Bean
    public Logger logger() {
        PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");

        FileAppender fileAppender = new FileAppender();
        fileAppender.setName("FileAppender");
        fileAppender.setFile("error.log");
        fileAppender.setLayout(layout);
        fileAppender.setAppend(true);
        fileAppender.activateOptions();

        Logger.getRootLogger().addAppender(fileAppender);
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.addAppender(fileAppender);
        rootLogger.setLevel(Level.ERROR);
        return rootLogger;
    }
}
