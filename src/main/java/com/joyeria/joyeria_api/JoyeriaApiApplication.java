package com.joyeria.joyeria_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JoyeriaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(JoyeriaApiApplication.class, args);
	}

}
