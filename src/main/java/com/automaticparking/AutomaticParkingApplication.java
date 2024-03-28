package com.automaticparking;


import config.configDB;
import model.test.abcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutomaticParkingApplication {
	public static void main(String[] args) {

		SpringApplication.run(AutomaticParkingApplication.class, args);
		new configDB();


		abcService.main(args);
	}
}
