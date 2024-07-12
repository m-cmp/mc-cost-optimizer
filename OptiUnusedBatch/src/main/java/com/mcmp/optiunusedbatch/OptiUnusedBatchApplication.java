package com.mcmp.optiunusedbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableBatchProcessing
public class OptiUnusedBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(OptiUnusedBatchApplication.class, args);
    }

}
