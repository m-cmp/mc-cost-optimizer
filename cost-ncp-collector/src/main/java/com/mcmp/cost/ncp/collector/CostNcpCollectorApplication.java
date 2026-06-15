package com.mcmp.cost.ncp.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class CostNcpCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CostNcpCollectorApplication.class, args);
    }

}
