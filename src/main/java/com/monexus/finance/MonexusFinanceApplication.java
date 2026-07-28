package com.monexus.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MonexusFinanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonexusFinanceApplication.class, args);
	}

}
