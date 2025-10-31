package com.mcmp.cost.azure.collector.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AzureApiCredentialDto {

    /**
     * 테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    private String tenantId;

    /**
     * 클라이언트 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    private String clientId;

    /**
     * client secret. ex) aaaaaaaaaaaaaaaaaaaaaaaaaaaaa
     */
    private String clientSecret;

    /**
     * subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    private String subscriptionId;
}
