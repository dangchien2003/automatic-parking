package com.automaticparking;


import config.configDB;
import com.automaticparking.test.abcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import util.hibernateUtil;

@SpringBootApplication
public class AutomaticParkingApplication {
	public static void main(String[] args) {

		SpringApplication.run(AutomaticParkingApplication.class, args);
		new configDB();


		abcService.main(args);
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
