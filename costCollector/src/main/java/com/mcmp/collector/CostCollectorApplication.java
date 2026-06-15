package com.mcmp.collector;

import com.mcmp.collector.properties.AlarmServiceUrlProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AlarmServiceUrlProperties.class)
public class CostCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CostCollectorApplication.class, args);
    }

}
