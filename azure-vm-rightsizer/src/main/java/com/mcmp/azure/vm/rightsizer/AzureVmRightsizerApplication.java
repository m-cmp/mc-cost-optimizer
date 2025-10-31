package com.mcmp.azure.vm.rightsizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class AzureVmRightsizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzureVmRightsizerApplication.class, args);
    }

}
