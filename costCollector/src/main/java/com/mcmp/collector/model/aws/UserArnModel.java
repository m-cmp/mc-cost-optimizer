package com.mcmp.collector.model.aws;

import lombok.Data;

@Data
public class UserArnModel {

    private String mcmp_id;
    private String csp;
    private String role_arn;
}
