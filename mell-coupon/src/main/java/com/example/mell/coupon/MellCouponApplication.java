package com.example.mell.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;



@SpringBootApplication
public class MellCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MellCouponApplication.class, args);
    }

}
