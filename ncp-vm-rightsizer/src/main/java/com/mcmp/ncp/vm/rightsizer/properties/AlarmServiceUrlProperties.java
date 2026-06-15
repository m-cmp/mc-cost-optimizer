package com.mcmp.ncp.vm.rightsizer.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "costopti.alarmservice")
public class AlarmServiceUrlProperties {

    private final String url;
}
