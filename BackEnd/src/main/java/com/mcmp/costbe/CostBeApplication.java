package com.mcmp.costbe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CostBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CostBeApplication.class, args);

    }
}
