package com.automaticparking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import util.hibernateUtil;

@SpringBootApplication
public class AutomaticParkingApplication {
	public static void main(String[] args) {
		SpringApplication.run(AutomaticParkingApplication.class, args);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
			System.out.println("closing database...");
			try {
				hibernateUtil.shutdown();
			}catch (Exception e) {
				System.out.println("cannot close database ");
			}
			System.out.println("closed database");
			}
		});
	}
}

