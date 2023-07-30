package com.example.melll.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MellThirdPartyApplication {

	public static void main(String[] args) {

		SpringApplication.run(MellThirdPartyApplication.class, args);


	}

}
