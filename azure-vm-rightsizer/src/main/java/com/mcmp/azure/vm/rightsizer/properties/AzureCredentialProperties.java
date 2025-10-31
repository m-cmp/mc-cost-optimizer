package com.mcmp.azure.vm.rightsizer.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "azure.credential")
public class AzureCredentialProperties {

    /**
     * 테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    private final String tenantId;

    /**
     * 클라이언트 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    private final String clientId;

    /**
     * client secret. ex) aaaaaaaaaaaaaaaaaaaaaaaaaaaaa
     */
    private final String clientSecret;

    /**
     * subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    private final String subscriptionId;
}
