package com.mcmp.costbe.archive.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 아카이브 S3 정적 설정(env). 버킷/role_arn 은 temp_cmp_user_role_arn 에서 조회하므로 여기 없음.
 * - prefix : CUR 데이터와 구분하는 전용 접두어 (invoice-archive)
 * - region : 버킷 리전
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "archive.s3")
public class ArchiveS3Properties {
    private String prefix = "invoice-archive";
    private String region = "ap-northeast-2";
}
