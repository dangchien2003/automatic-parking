package com.automaticparking;


import com.automaticparking.model.staff.Staff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.configDB;
import com.automaticparking.test.abcService;
import encrypt.JWT;
import io.jsonwebtoken.Claims;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import util.Genarate;
import util.hibernateUtil;

import java.io.IOException;
import java.util.Map;

@SpringBootApplication
public class AutomaticParkingApplication {
	public static void main(String[] args) {




		SpringApplication.run(AutomaticParkingApplication.class, args);
		new configDB();

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

