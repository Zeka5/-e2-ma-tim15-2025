package com.ma.ma_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaBackendApplication.class, args);
	}

}
