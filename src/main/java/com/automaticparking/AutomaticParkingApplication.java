package com.automaticparking;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j
public class AutomaticParkingApplication {
//    private Logger logger;

    public static void main(String[] args) {
        SpringApplication.run(AutomaticParkingApplication.class, args);

        // Sử dụng logger để ghi lỗi
        log.error("Đây là log lỗi1.");
    }
}

