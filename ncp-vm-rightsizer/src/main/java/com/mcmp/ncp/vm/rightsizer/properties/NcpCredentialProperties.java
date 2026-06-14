package com.mcmp.ncp.vm.rightsizer.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 자동 바인딩(@ConfigurationProperties) 대신 NcpCredentialConfig 의 @Bean 에서
// CredentialResolver(openbao.enabled 정책)로 구성한다.
@Getter
@RequiredArgsConstructor
public class NcpCredentialProperties {

    /**
     * NCP AccessKey. ex) ncp_iam_abcedfghijklmopq12341234
     */
    private final String iamAccessKey;

    /**
     * NCP SecretKey. ex) ncp_iam_abcedfghijklmopq1234123430430403403
     */
    private final String iamSecretKey;
}
