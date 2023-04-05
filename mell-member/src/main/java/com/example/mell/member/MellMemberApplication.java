package com.example.mell.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.mell.member.feign")
public class MellMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MellMemberApplication.class, args);
    }

}
