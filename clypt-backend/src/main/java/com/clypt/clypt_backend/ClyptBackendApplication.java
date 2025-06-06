package com.clypt.clypt_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClyptBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClyptBackendApplication.class, args);
	}

}
