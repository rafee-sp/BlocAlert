package com.rafee.blocalert.blocalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlocalertAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlocalertAppApplication.class, args);
	}

}
