package com.mcmp.cost.azure.collector.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "azure.ssl.verify")
public class AzureSslProperties {
    private boolean disabled = false;
}
