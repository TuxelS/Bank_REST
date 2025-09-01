package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.example.bankcards")
@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
