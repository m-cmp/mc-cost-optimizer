package com.mcmp.costbe.archive.model;

import lombok.Data;

/**
 * temp_cmp_user_role_arn(csp='AWS') 조회 결과.
 * 아카이브 목적지 버킷 + AssumeRole 대상 role_arn — CUR 읽기와 동일한 접근 정보를 재사용한다.
 */
@Data
public class UserArnModel {
    private String bucketNm;   // 아카이브 저장 버킷 (CUR 버킷 재사용)
    private String roleArn;    // AssumeRole 대상 role
}
