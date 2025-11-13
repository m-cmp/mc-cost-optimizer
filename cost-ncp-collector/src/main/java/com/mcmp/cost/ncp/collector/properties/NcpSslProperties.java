package com.mcmp.cost.ncp.collector.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ncp.ssl.verify")
public class NcpSslProperties {
    private boolean disabled = false;
}
