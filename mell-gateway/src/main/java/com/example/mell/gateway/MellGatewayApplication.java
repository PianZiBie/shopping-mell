package com.example.mell.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MellGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MellGatewayApplication.class, args);
	}

}
