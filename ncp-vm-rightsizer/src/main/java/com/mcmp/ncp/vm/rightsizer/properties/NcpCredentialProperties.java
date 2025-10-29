package com.mcmp.ncp.vm.rightsizer.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "ncp.credential")
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
