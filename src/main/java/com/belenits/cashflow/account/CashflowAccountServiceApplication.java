package com.belenits.cashflow.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CashflowAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashflowAccountServiceApplication.class, args);
	}

}
