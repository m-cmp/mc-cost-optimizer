package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NcpApiCredentialDto {

    /**
     * NCP AccessKey. ex) ncp_iam_abcedfghijklmopq12341234
     */
    private String iamAccessKey;

    /**
     * NCP SecretKey. ex) ncp_iam_abcedfghijklmopq1234123430430403403
     */
    private String iamSecretKey;
}
