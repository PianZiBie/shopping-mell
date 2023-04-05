package com.example.mell.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 逻辑删除:
 * 1.配制逻辑删除规则
 * 2.配置逻辑删除组件Bean
 * 3.加上逻辑删除注解@TableLogic
 */

@MapperScan("com.example.mell.product.dao")
@SpringBootApplication
public class MellProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MellProductApplication.class, args);
    }

}
