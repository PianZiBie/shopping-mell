package com.example.mell.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.mell.search.feign")
public class MellSearchApplication {
    public static void main(String[] args) {

        SpringApplication.run(MellSearchApplication.class, args);
    }


}
