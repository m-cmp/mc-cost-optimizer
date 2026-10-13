CREATE DATABASE IF NOT EXISTS cost;

USE cost;

-- cost.alarm_history definition

CREATE TABLE IF NOT EXISTS `alarm_history` (
                                 `event_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                 `resource_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                 `resource_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                 `occure_dt` timestamp NOT NULL,
                                 `account_id` varchar(100) DEFAULT NULL,
                                 `urgency` varchar(20) DEFAULT NULL,
                                 `plan` varchar(20) DEFAULT NULL,
                                 `note` varchar(200) DEFAULT NULL,
                                 `occure_date` timestamp NOT NULL,
                                 `csp_type` varchar(10) NOT NULL,
                                 `alarm_impl` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                 `project_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'BLANK_00_',
                                 PRIMARY KEY (`event_type`,`resource_id`,`resource_type`,`occure_date`,`csp_type`,`alarm_impl`,`project_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.recommendation_history definition

CREATE TABLE IF NOT EXISTS `recommendation_history` (
                                 `id`             bigint NOT NULL AUTO_INCREMENT,
                                 `ns_id`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                 `instance_id`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                 `recommendation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                 `response_json`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci,
                                 `created_at`     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_ns` (`ns_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.asset_compute_metric definition

CREATE TABLE IF NOT EXISTS `asset_compute_metric` (
                                        `csp_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                        `csp_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                        `csp_instanceid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                        `collect_dt` timestamp NOT NULL,
                                        `metric_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL COMMENT 'CPU/NETWORK',
                                        `metric_amount` double DEFAULT NULL,
                                        `resource_status` varchar(100) DEFAULT NULL,
                                        `resource_spot_yn` varchar(1) DEFAULT 'N',
                                        PRIMARY KEY (`csp_type`,`csp_instanceid`,`collect_dt`,`metric_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.asset_rsopt_settings definition

CREATE TABLE IF NOT EXISTS `asset_rsopt_settings` (
                                        `csp_type` varchar(100) DEFAULT NULL,
                                        `metric_type` varchar(100) DEFAULT NULL,
                                        `regress_duration` int DEFAULT NULL,
                                        `criteria_value` double DEFAULT NULL,
                                        `cmp_user_id` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.cur_origin definition

CREATE TABLE IF NOT EXISTS `cur_origin` (
                              `lineitem_usageaccountid` varchar(100) NOT NULL COMMENT 'AWS 서브계정 구분 ID',
                              `lineitem_productcode` varchar(100) NOT NULL COMMENT '서비스구분코드',
                              `lineitem_resourceid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL COMMENT '인스턴스 구분 코드',
                              `lineitem_lineitemtype` varchar(100) DEFAULT NULL COMMENT '비용구분',
                              `product_instancetype` varchar(100) DEFAULT NULL COMMENT '서비스 상세 구분',
                              `pricing_unit` varchar(100) DEFAULT NULL COMMENT '사용량 단위',
                              `lineitem_usageamount` varchar(100) DEFAULT NULL COMMENT '사용량',
                              `lineitem_unblendedcost` varchar(100) DEFAULT NULL COMMENT 'on-demand 비용',
                              `lineitem_blendedcost` varchar(100) DEFAULT NULL COMMENT '할인 청구 비용',
                              `lineitem_usagestartdate` timestamp NULL DEFAULT NULL COMMENT '인스턴스 사용 시작 일시',
                              `lineitem_usageenddate` timestamp NULL DEFAULT NULL COMMENT '인스턴스 사용 끝 일시',
                              `pricing_publicondemandcost` varchar(100) DEFAULT NULL COMMENT 'on-demand 총비용',
                              `pricing_publicondemandrate` varchar(100) DEFAULT NULL COMMENT 'on-demand 단가',
                              `lineitem_currencycode` varchar(20) DEFAULT NULL COMMENT '화폐단위',
                              `data_collect_date` timestamp NULL DEFAULT NULL,
                              `data_collect_seq` varchar(100) DEFAULT NULL,
                              `product_sku` varchar(100) DEFAULT NULL,
                              `product_region` varchar(50) DEFAULT NULL,
                              `product_instanceFamily` varchar(100) DEFAULT NULL,
                              `product_location` varchar(100) DEFAULT NULL,
                              `lineitem_operation` varchar(100) DEFAULT NULL,
                              `product_instancetypefamily` varchar(20) DEFAULT NULL,
                              `lineitem_usagetype` varchar(100) DEFAULT NULL,
                              `product_vcpu` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                              `product_memory` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.cur_process_info definition

CREATE TABLE IF NOT EXISTS `cur_process_info` (
                                    `csp` varchar(10) NOT NULL,
                                    `payer_account` varchar(100) NOT NULL,
                                    `collect_date` varchar(10) NOT NULL,
                                    `object_key` varchar(200) DEFAULT NULL,
                                    `certifed_fixed_yn` varchar(1) DEFAULT NULL,
                                    `certifed_fixed_date` timestamp NULL DEFAULT NULL,
                                    PRIMARY KEY (`csp`,`payer_account`,`collect_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

INSERT INTO cur_process_info (csp, payer_account, collect_date, object_key, certifed_fixed_yn, certifed_fixed_date)
VALUES ('AWS', 'mcmpcostopti', DATE_FORMAT(NOW() , '%Y%m'), NULL, 'N', NULL)
    ON DUPLICATE KEY UPDATE collect_date = VALUES(collect_date);

-- cost.daily_abnormal_by_product definition

CREATE TABLE IF NOT EXISTS `daily_abnormal_by_product` (
                                             `collect_dt` date NOT NULL,
                                             `product_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                             `abnormal_rating` varchar(100) DEFAULT NULL,
                                             `percentage_point` varchar(100) DEFAULT NULL,
                                             `standard_cost` varchar(100) DEFAULT NULL,
                                             `subject_cost` varchar(100) DEFAULT NULL,
                                             `project_cd` varchar(100) NOT NULL,
                                             `workspace_cd` varchar(100) DEFAULT NULL,
                                             `csp_type` varchar(10) DEFAULT NULL,
                                             PRIMARY KEY (`collect_dt`,`product_cd`,`project_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.daily_summation_by_product definition

CREATE TABLE IF NOT EXISTS `daily_summation_by_product` (
                                              `date` date NOT NULL,
                                              `product` varchar(100) NOT NULL,
                                              `total_cost` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                              `project_cd` varchar(100) NOT NULL,
                                              `workspace_cd` varchar(100) DEFAULT NULL,
                                              `csp_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                              PRIMARY KEY (`date`,`product`,`project_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.inst_opti_rcmd_rst definition

CREATE TABLE IF NOT EXISTS `inst_opti_rcmd_rst` (
                                      `create_dt` timestamp NOT NULL,
                                      `resource_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                      `csp_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                      `csp_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                      `origin_type` varchar(100) DEFAULT NULL,
                                      `rcmd_type` varchar(100) DEFAULT NULL,
                                      `plan_type` varchar(20) DEFAULT NULL,
                                      `origin_usd` varchar(100) DEFAULT NULL,
                                      `rcmd_usd` varchar(100) DEFAULT NULL,
                                      PRIMARY KEY (`create_dt`,`resource_id`,`csp_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.monthly_summation definition

CREATE TABLE IF NOT EXISTS `monthly_summation` (
                                     `year_month` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                     `project_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                     `csp` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                     `total_cost` varchar(100) DEFAULT NULL,
                                     `workspace_cd` varchar(100) DEFAULT NULL,
                                     PRIMARY KEY (`year_month`,`project_cd`,`csp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.service_category definition

CREATE TABLE IF NOT EXISTS `service_category` (
                                    `service_cd` varchar(100) NOT NULL COMMENT '서비스 코드',
                                    `service_nm` varchar(100) NOT NULL COMMENT '서비스 명',
                                    `service_type` varchar(100) NOT NULL COMMENT '서비스 타입',
                                    PRIMARY KEY (`service_cd`,`service_nm`,`service_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.servicegroup_meta definition

CREATE TABLE IF NOT EXISTS `servicegroup_meta` (
                                          `csp_type` varchar(100) COLLATE utf8mb4_unicode_520_ci NOT NULL COMMENT 'CSP 종류',
                                          `csp_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT 'mcmpcostopti' COMMENT 'CSP 계정 ID',
                                          `csp_instanceid` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'common' COMMENT '인스턴스 구분코드 = CSP 과금 데이터의 자원 식별자 (AWS: i-…/EKS ARN/버킷명, Azure: ARM ID, GCP: 인스턴스명/클러스터명/버킷명, NCP: instanceNo/NKS UUID/버킷명). Azure ARM ID 최대 ~274자',
                                          `service_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT 'undefined' COMMENT '서비스 코드',
                                          `service_nm` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL COMMENT '서비스 명',
                                          `service_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT 'project' COMMENT '서비스 타입',
                                          `workspace_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT 'undefined' COMMENT 'WorkSpace 코드',
                                          `service_uid` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                          `vm_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT 'undefined',
                                          `vm_uid` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                          `vm_nm` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                          `mci_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT 'undefined',
                                          `mci_uid` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                          `mci_nm` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                          `instance_running_status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT 'N',
                                          PRIMARY KEY (`csp_type`,`csp_instanceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.tbl_table_billing_detail_202409 definition

CREATE TABLE IF NOT EXISTS `tbl_table_billing_detail_202409` (
                                                   `lineitem_usageaccountid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_productcode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_resourceid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_lineitemtype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `product_instancetype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `pricing_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_usageamount` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_unblendedcost` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_blendedcost` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_usagestartdate` timestamp NULL DEFAULT NULL,
                                                   `lineitem_usageenddate` timestamp NULL DEFAULT NULL,
                                                   `pricing_publicondemandcost` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `pricing_publicondemandrate` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `lineitem_currencycode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                                   `product_sku` varchar(100) DEFAULT NULL,
                                                   `product_region` varchar(50) DEFAULT NULL,
                                                   `product_instanceFamily` varchar(100) DEFAULT NULL,
                                                   `product_location` varchar(100) DEFAULT NULL,
                                                   `lineitem_operation` varchar(100) DEFAULT NULL,
                                                   `product_instancetypefamily` varchar(20) DEFAULT NULL,
                                                   `lineitem_usagetype` varchar(100) DEFAULT NULL,
                                                   `product_vcpu` varchar(30) DEFAULT NULL,
                                                   `product_memory` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.temp_cmp_user_info definition

CREATE TABLE IF NOT EXISTS `temp_cmp_user_info` (
                                      `mcmp_user_id` varchar(100) DEFAULT NULL,
                                      `csp_type` varchar(10) DEFAULT NULL,
                                      `csp_account_id` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

INSERT INTO cost.temp_cmp_user_info(mcmp_user_id, csp_type, csp_account_id)
SELECT 'mcmpcostopti', 'AWS', 'mcmpcostopti'
WHERE NOT EXISTS (
    SELECT 1 FROM cost.temp_cmp_user_info
    WHERE mcmp_user_id = 'mcmpcostopti'
      AND csp_type = 'AWS'
      AND csp_account_id = 'mcmpcostopti'
);

-- cost.temp_cmp_user_mail_receiver definition

CREATE TABLE IF NOT EXISTS `temp_cmp_user_mail_receiver` (
                                               `mcmp_user_id` varchar(100) DEFAULT NULL,
                                               `mcmp_mail_receiver` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.temp_cmp_user_role_arn definition

CREATE TABLE IF NOT EXISTS `temp_cmp_user_role_arn` (
                                          `mcmp_user_id` varchar(100) DEFAULT NULL,
                                          `csp` varchar(20) DEFAULT NULL,
                                          `bucket_nm` varchar(100) DEFAULT NULL,
                                          `role_arn` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.unused_batch_rst definition

CREATE TABLE IF NOT EXISTS `unused_batch_rst` (
                                    `create_dt` timestamp NOT NULL,
                                    `csp_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                    `csp_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                    `csp_instanceid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                    `plan_type` varchar(100) DEFAULT NULL,
                                    PRIMARY KEY (`create_dt`,`csp_type`,`csp_account`,`csp_instanceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.unused_collector definition

CREATE TABLE IF NOT EXISTS `unused_collector` (
                                    `csp_resourceid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
                                    `create_dt` timestamp NOT NULL,
                                    `instance_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
                                    `rsrc_type` varchar(10) DEFAULT NULL,
                                    `region_id` varchar(50) DEFAULT NULL,
                                    `region_nm` varchar(100) DEFAULT NULL,
                                    `operation` varchar(100) DEFAULT NULL,
                                    `instance_family_type` varchar(20) DEFAULT NULL,
                                    `product_sku` varchar(100) DEFAULT NULL,
                                    `instance_family` varchar(100) DEFAULT NULL,
                                    PRIMARY KEY (`csp_resourceid`,`create_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- cost.unused_process_mart definition (AWS 미사용 자원 탐지용 - costProcessor, costSelector에서 사용)

CREATE TABLE IF NOT EXISTS `unused_process_mart` (
                                       `create_dt` timestamp NOT NULL,
                                       `resource_id` varchar(200) NOT NULL,
                                       `collect_dt` timestamp NOT NULL,
                                       `metric_type` varchar(100) NOT NULL,
                                       `metric_avg_amount` double DEFAULT NULL,
                                       PRIMARY KEY (`resource_id`,`collect_dt`,`metric_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `unused_daily_mart` (
    `create_dt`         timestamp       NOT NULL        COMMENT '배치 실행 시각',
    `csp_type`          varchar(100)    NOT NULL        COMMENT 'CSP 종류 (AZURE, NCP, GCP 등)',
    `resource_id`       varchar(200)    NOT NULL        COMMENT '인스턴스/VM ID',
    `collect_dt`        date            NOT NULL        COMMENT '메트릭 수집 날짜',
    `metric_type`       varchar(100)    NOT NULL        COMMENT '메트릭 종류 (cpu 등)',
    `metric_avg_amount` double          DEFAULT NULL    COMMENT '일별 평균 메트릭 값',
    PRIMARY KEY (`csp_type`, `resource_id`, `collect_dt`, `metric_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci COMMENT='미사용 자원 탐지용 일별 메트릭 누적 테이블';

-- cost.budget_monthly definition
-- 프로젝트별 월간 예산 설정 테이블 (AWS, AZURE, NCP, GCP 공통)
-- project_cd: servicegroup_meta.service_cd 와 동일한 값 사용
-- UNIQUE KEY: (csp, project_cd, year, month) - CSP+프로젝트+연월 조합으로 유일

create table if not exists budget_monthly
(
    id         bigint auto_increment primary key,
    csp        varchar(50)                                              not null comment 'CSP 종류 (AWS, AZURE, NCP, GCP)',
    year       int                                                      not null comment '연도',
    month      int                                                      not null comment '월',
    project_cd varchar(100) collate utf8mb4_unicode_520_ci             not null comment '프로젝트 코드 (servicegroup_meta.service_cd)',
    budget     decimal(18, 3) default 0.000                            null     comment '예산 금액',
    currency   varchar(10)    default 'USD'                            not null comment '통화 (USD, KRW)',
    created_at timestamp      default current_timestamp()              null,
    updated_at timestamp      default current_timestamp()              null on update current_timestamp(),
    constraint uq_csp_project_year_month unique (csp, project_cd, year, month)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_520_ci comment = '프로젝트별 월간 예산 설정';

-- ============================================================
-- cost-ncp-collector module
--   NCP 비용 수집 결과 테이블
-- ============================================================

CREATE TABLE IF NOT EXISTS `ncp_cost_service_month` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '아이디',
    `created` datetime DEFAULT NULL COMMENT 'row insert 시간',
    `updated` datetime DEFAULT NULL COMMENT 'row update 시간',
    `member_no` varchar(255) NOT NULL COMMENT '회원 번호. ex) 0000000',
    `product_demand_type` varchar(255) NOT NULL COMMENT '상품 청구 유형 이름. ex) Virtual Private Cloud',
    `demand_month` varchar(255) NOT NULL COMMENT '청구 월. ex) 202509',
    `use_amount` double NOT NULL COMMENT '사용 금액. ex) 2580.0',
    `demand_amount` double NOT NULL COMMENT '청구 금액. ex) 2580.0',
    `write_date` datetime NOT NULL COMMENT '작성 일시(YYYY-MM-DDThh:mm:ssZ). ex) 2025-09-11 08:02:40+0900',
    `pay_currency` varchar(255) NOT NULL COMMENT '결제 통화. ex) KRW',
    PRIMARY KEY (`id`) COMMENT '서비스별 청구 비용 목록'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE IF NOT EXISTS `ncp_cost_vm_month` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '아이디',
    `created` datetime DEFAULT NULL COMMENT 'row insert 시간',
    `updated` datetime DEFAULT NULL COMMENT 'row update 시간',
    `member_no` varchar(255) NOT NULL COMMENT '회원 번호. ex) 0000000',
    `demand_month` varchar(255) NOT NULL COMMENT '청구 월. ex) 202509',
    `instance_no` varchar(255) NOT NULL COMMENT '인스턴스 번호. ex) 00000000',
    `instance_name` varchar(255) NOT NULL COMMENT '인스턴스 이름. ex) dongwoo-abc-abc-abc',
    `usage_unit_code` varchar(255) NOT NULL COMMENT '사용량 단위 코드. ex) USAGE_HH',
    `usage_unit_name` varchar(255) NOT NULL COMMENT '사용량 단위 이름. ex) Usage time (per hour)',
    `product_price` double NOT NULL COMMENT '상품 가격. ex) 123',
    `unit_usage_quantity` double NOT NULL COMMENT '단위 사용량. ex) 240',
    `total_unit_usage_quantity` double NOT NULL COMMENT '총 단위 사용량. ex) 240',
    `use_amount` double NOT NULL COMMENT '사용 금액. ex) 29520',
    `demand_amount` double NOT NULL COMMENT '청구 금액. ex) 29520',
    `write_date` datetime NOT NULL COMMENT '작성 일시(YYYY-MM-DDThh:mm:ssZ). ex) 2025-09-11 08:02:40+0900',
    `pay_currency` varchar(255) NOT NULL COMMENT '결제 통화. ex) KRW',
    `region_code` varchar(10) NOT NULL DEFAULT 'KR' COMMENT '리전 코드. ex) KR',
    `server_spec_code` varchar(100) NOT NULL DEFAULT '' COMMENT '서버 스펙 코드. ex) s2-g2-s50',
    PRIMARY KEY (`id`) COMMENT 'VM별 청구 비용 목록'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE IF NOT EXISTS `ncp_cost_vm_daily` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created` timestamp NULL DEFAULT current_timestamp() COMMENT '생성일시',
    `updated` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
    `member_no` varchar(100) NOT NULL COMMENT '회원 번호',
    `demand_month` varchar(6) NOT NULL COMMENT '청구 월. ex) 202510',
    `region_code` varchar(10) NOT NULL COMMENT '리전 코드. ex) KR, JPN',
    `server_spec_code` varchar(100) NOT NULL COMMENT '서버 스펙 코드. ex) s2-g2-s50',
    `instance_no` varchar(100) NOT NULL COMMENT '인스턴스 번호',
    `instance_name` varchar(200) NOT NULL COMMENT '인스턴스 이름',
    `usage_unit_code` varchar(50) NOT NULL COMMENT '사용량 단위 코드. ex) USAGE_HH',
    `usage_unit_name` varchar(100) NOT NULL COMMENT '사용량 단위 이름',
    `product_price` double NOT NULL COMMENT '상품 가격',
    `unit_usage_quantity` double NOT NULL COMMENT '단위 사용량',
    `total_unit_usage_quantity` double NOT NULL COMMENT '총 단위 사용량',
    `use_amount` double NOT NULL COMMENT '사용 금액(누적)',
    `demand_amount` double NOT NULL COMMENT '청구 금액(누적)',
    `write_date` timestamp NOT NULL COMMENT '작성 일시',
    `pay_currency` varchar(10) NOT NULL COMMENT '결제 통화. ex) KRW',
    `target_date` date NOT NULL COMMENT '대상 날짜 (일별)',
    `daily_charge_amount` double NOT NULL COMMENT '일별 청구 금액 (전일 대비 차이)',
    PRIMARY KEY (`id`),
    KEY `idx_instance_date` (`instance_no`,`target_date`),
    KEY `idx_target_date` (`target_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci COMMENT='NCP VM 일별 비용 데이터 (월별 데이터에서 계산)';

-- NCP 자원(K8S / OBJECT_STORAGE) 계약별 월 누적 청구 비용 — ncp_cost_vm_month 자매 테이블 (server_spec_code 없음)
--   resource_id = servicegroup_meta.csp_instanceid 조인 키 (K8S = NKS 클러스터 UUID, OBJECT_STORAGE = member_no[계정 단위])
--   UNIQUE 키가 재실행 dedupe 의 실체 (snapshot_date = DATE(write_date), 하루 1 스냅샷/키)
CREATE TABLE IF NOT EXISTS `ncp_cost_resource_month` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '아이디',
    `created` datetime DEFAULT NULL COMMENT 'row insert 시간',
    `updated` datetime DEFAULT NULL COMMENT 'row update 시간',
    `member_no` varchar(100) NOT NULL COMMENT '회원 번호',
    `demand_month` varchar(6) NOT NULL COMMENT '청구 월. ex) 202510',
    `region_code` varchar(10) NOT NULL DEFAULT 'KR' COMMENT '리전 코드',
    `resource_type` varchar(20) NOT NULL COMMENT 'K8S | OBJECT_STORAGE',
    `demand_type_code` varchar(20) NOT NULL COMMENT '청구 유형 코드. ex) OSSM',
    `demand_type_name` varchar(100) DEFAULT NULL COMMENT '청구 유형 이름',
    `demand_type_detail_code` varchar(20) NOT NULL DEFAULT '' COMMENT '청구 유형 상세 코드',
    `demand_type_detail_name` varchar(100) DEFAULT NULL COMMENT '청구 유형 상세 이름',
    `contract_no` varchar(50) NOT NULL DEFAULT '' COMMENT '계약 번호',
    `product_code` varchar(50) DEFAULT NULL COMMENT '상품 코드 (contractProductList[0].productCode)',
    `product_name` varchar(200) DEFAULT NULL COMMENT '상품 이름 (productItemKind.codeName)',
    `instance_no` varchar(100) DEFAULT NULL COMMENT 'NCP 인스턴스 번호(원본)',
    `instance_name` varchar(200) DEFAULT NULL COMMENT 'NCP 인스턴스 이름(원본, contract.instanceName)',
    `resource_id` varchar(200) NOT NULL COMMENT 'servicegroup_meta.csp_instanceid 조인 키 (K8S = NKS 클러스터 UUID)',
    `resource_name` varchar(200) DEFAULT NULL COMMENT '자원 표시 이름 (K8S = 클러스터명)',
    `resource_id_source` varchar(20) NOT NULL COMMENT 'resource_id 출처. NKS_UUID | INSTANCE_NO | CONTRACT_NO | MEMBER_NO',
    `usage_unit_code` varchar(50) NOT NULL DEFAULT '' COMMENT '사용량 단위 코드',
    `usage_unit_name` varchar(100) NOT NULL DEFAULT '' COMMENT '사용량 단위 이름',
    `product_price` double NOT NULL DEFAULT 0 COMMENT '상품 가격',
    `unit_usage_quantity` double NOT NULL DEFAULT 0 COMMENT '단위 사용량',
    `total_unit_usage_quantity` double NOT NULL DEFAULT 0 COMMENT '총 단위 사용량',
    `use_amount` double NOT NULL COMMENT '사용 금액(월 누적, KRW)',
    `demand_amount` double NOT NULL COMMENT '청구 금액(월 누적, KRW)',
    `write_date` datetime NOT NULL COMMENT 'NCP 작성 일시',
    `snapshot_date` date NOT NULL COMMENT 'DATE(write_date). 재실행 dedupe 키',
    `pay_currency` varchar(10) NOT NULL COMMENT '결제 통화. ex) KRW',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ncp_cost_resource_month` (`member_no`,`demand_month`,`resource_type`,`resource_id`,`region_code`,`demand_type_detail_code`,`contract_no`,`snapshot_date`),
    KEY `idx_ncr_month_type_rid` (`resource_type`,`resource_id`,`demand_month`),
    KEY `idx_ncr_month_month_rid` (`demand_month`,`resource_id`,`id`),
    KEY `idx_ncr_month_write` (`write_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci COMMENT='NCP 자원(K8S/ObjectStorage)별 월 누적 청구 비용';

-- NCP 자원(K8S / OBJECT_STORAGE) 일별 비용 — ncp_cost_vm_daily 와 동일하게 월 누적의 전일 차분
CREATE TABLE IF NOT EXISTS `ncp_cost_resource_daily` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '아이디',
    `created` datetime DEFAULT NULL COMMENT 'row insert 시간',
    `updated` datetime DEFAULT NULL COMMENT 'row update 시간',
    `member_no` varchar(100) NOT NULL COMMENT '회원 번호',
    `demand_month` varchar(6) NOT NULL COMMENT '청구 월. ex) 202510',
    `region_code` varchar(10) NOT NULL DEFAULT 'KR' COMMENT '리전 코드',
    `resource_type` varchar(20) NOT NULL COMMENT 'K8S | OBJECT_STORAGE',
    `demand_type_code` varchar(20) NOT NULL COMMENT '청구 유형 코드',
    `demand_type_name` varchar(100) DEFAULT NULL,
    `demand_type_detail_code` varchar(20) NOT NULL DEFAULT '' COMMENT '청구 유형 상세 코드',
    `demand_type_detail_name` varchar(100) DEFAULT NULL,
    `contract_no` varchar(50) NOT NULL DEFAULT '' COMMENT '계약 번호',
    `product_code` varchar(50) DEFAULT NULL,
    `product_name` varchar(200) DEFAULT NULL,
    `instance_no` varchar(100) DEFAULT NULL COMMENT 'NCP 인스턴스 번호(원본)',
    `instance_name` varchar(200) DEFAULT NULL COMMENT 'NCP 인스턴스 이름(원본)',
    `resource_id` varchar(200) NOT NULL COMMENT 'servicegroup_meta.csp_instanceid 조인 키',
    `resource_name` varchar(200) DEFAULT NULL,
    `resource_id_source` varchar(20) NOT NULL COMMENT 'NKS_UUID | INSTANCE_NO | CONTRACT_NO | MEMBER_NO',
    `usage_unit_code` varchar(50) NOT NULL DEFAULT '',
    `usage_unit_name` varchar(100) NOT NULL DEFAULT '',
    `product_price` double NOT NULL DEFAULT 0,
    `unit_usage_quantity` double NOT NULL DEFAULT 0,
    `total_unit_usage_quantity` double NOT NULL DEFAULT 0,
    `use_amount` double NOT NULL COMMENT '사용 금액(누적, KRW)',
    `demand_amount` double NOT NULL COMMENT '청구 금액(누적, KRW)',
    `write_date` datetime NOT NULL COMMENT 'NCP 작성 일시',
    `pay_currency` varchar(10) NOT NULL COMMENT '결제 통화. ex) KRW',
    `target_date` date NOT NULL COMMENT '대상 날짜 (= DATE(write_date))',
    `daily_charge_amount` double NOT NULL COMMENT '일별 청구 금액 (전일 누적 대비 차이, KRW)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ncp_cost_resource_daily` (`member_no`,`demand_month`,`resource_type`,`resource_id`,`region_code`,`demand_type_detail_code`,`contract_no`,`target_date`),
    KEY `idx_ncr_daily_type_rid_date` (`resource_type`,`resource_id`,`target_date`),
    KEY `idx_ncr_daily_rid_date` (`resource_id`,`target_date`),
    KEY `idx_ncr_daily_target_date` (`target_date`),
    KEY `idx_ncr_daily_month` (`demand_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci COMMENT='NCP 자원(K8S/ObjectStorage) 일별 비용 (월 누적 차분)';


-- ============================================================
-- cost-azure-collector module
--   Azure 비용 수집 결과 테이블
-- ============================================================

CREATE TABLE IF NOT EXISTS `azure_cost_service_daily` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '아이디',
    `created` datetime DEFAULT NULL COMMENT 'row insert 시간',
    `updated` datetime DEFAULT NULL COMMENT 'row update 시간',
    `tenant_id` varchar(255) NOT NULL COMMENT '테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000',
    `subscription_id` varchar(255) NOT NULL COMMENT 'subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000',
    `pre_tax_cost` double NOT NULL COMMENT '비용. ex) 16345.824',
    `usage_date` varchar(255) NOT NULL COMMENT '날짜. ex) 20250903',
    `service_name` varchar(255) NOT NULL COMMENT '서비스 이름. ex) Virtual Machines',
    `currency` varchar(255) NOT NULL COMMENT '통화 단위. ex) KRW',
    PRIMARY KEY (`id`) COMMENT 'Azure Service 일별 요금을 목록'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE IF NOT EXISTS `azure_cost_vm_daily` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '아이디',
    `created` datetime DEFAULT NULL COMMENT 'row insert 시간',
    `updated` datetime DEFAULT NULL COMMENT 'row update 시간',
    `tenant_id` varchar(255) NOT NULL COMMENT '테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000',
    `subscription_id` varchar(255) NOT NULL COMMENT 'subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000',
    `pre_tax_cost` double NOT NULL COMMENT '비용. ex) 16345.824',
    `usage_date` varchar(255) NOT NULL COMMENT '날짜. ex) 20250903',
    `resource_group_name` varchar(255) NOT NULL COMMENT '리소스 그룹. ex) rg-dongwoo-1',
    `resource_id` varchar(255) NOT NULL COMMENT '리소스 아이디.(AWS의 ARN과 비슷)',
    `region` varchar(255) NOT NULL COMMENT 'region 명. ex) koreacentral',
    `instance_type` varchar(255) NOT NULL COMMENT '인스턴스 타입. ex) Standard_DS3_v2',
    `os_type` varchar(255) NOT NULL COMMENT '인스턴스 os 타입. ex) WINDOWS',
    `vm_id` varchar(255) NOT NULL COMMENT 'vm 아이디 ex) vm-1',
    `resource_guid` varchar(255) NOT NULL COMMENT '리소스 고유 아이디. ex) 00000000-0000-0000-0000-00000000000',
    `currency` varchar(255) NOT NULL COMMENT '통화 단위. ex) KRW',
    PRIMARY KEY (`id`) COMMENT 'Azure Virtual Machines 별 요금 목록'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Azure 비-VM 자원(AKS managed cluster, Storage Account) 별 일별 요금 — azure_cost_vm_daily 자매 테이블 (VM 전용 컬럼 없음)
--   resource_id = Cost Management 반환값(소문자) 원문. BE 는 LOWER(servicegroup_meta.csp_instanceid) 와 비교
--   UNIQUE (subscription_id, resource_id, usage_date) + 수집기 DELETE→INSERT 로 재수집 멱등 (VM 테이블은 UNIQUE 없음)
--   collation 은 servicegroup_meta 와 같은 utf8mb4_unicode_520_ci (BE 조인 캐스팅 최소화; 이웃 Azure 테이블의 uca1400 과 다름)
CREATE TABLE IF NOT EXISTS `azure_cost_resource_daily` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '아이디',
    `created` datetime DEFAULT NULL COMMENT 'row insert 시간',
    `updated` datetime DEFAULT NULL COMMENT 'row update 시간',
    `tenant_id` varchar(255) NOT NULL COMMENT '테넌트 아이디',
    `subscription_id` varchar(36) NOT NULL COMMENT 'subscriptions 아이디',
    `pre_tax_cost` double NOT NULL COMMENT '비용(자원·일 합계, KRW)',
    `usage_date` varchar(8) NOT NULL COMMENT '날짜(yyyyMMdd). ex) 20250903',
    `resource_group_name` varchar(255) NOT NULL COMMENT '리소스 그룹',
    `resource_id` varchar(512) NOT NULL COMMENT 'ARM 리소스 아이디(소문자 원문). ex) /subscriptions/.../providers/microsoft.containerservice/managedclusters/aks-1',
    `resource_name` varchar(255) NOT NULL COMMENT 'resource_id 마지막 세그먼트 (클러스터명 / 스토리지 계정명)',
    `resource_type` varchar(20) NOT NULL COMMENT 'K8S | K8S_NODE | OBJECT_STORAGE',
    `service_name` varchar(255) NOT NULL COMMENT 'Azure 서비스명. ex) Azure Kubernetes Service, Storage',
    `region` varchar(255) DEFAULT NULL COMMENT 'ResourceLocation. ex) koreacentral',
    `currency` varchar(255) NOT NULL COMMENT '통화 단위. ex) KRW',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_azure_cost_resource_daily` (`subscription_id`,`resource_id`,`usage_date`),
    KEY `idx_acr_date_type` (`usage_date`,`resource_type`),
    KEY `idx_acr_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci COMMENT='Azure 비-VM 자원(AKS, Storage Account) 별 일별 요금';


-- ============================================================
-- gcpCollector module
--   GCP BigQuery 빌링 원본 수집 테이블
-- ============================================================

CREATE TABLE IF NOT EXISTS `gcp_billing_raw` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `created` datetime DEFAULT NULL COMMENT '수집 시각',
    `billing_account_id` varchar(255) DEFAULT NULL COMMENT '청구 계정 ID',
    `cost` double DEFAULT NULL COMMENT '발생 비용',
    `cost_type` varchar(50) DEFAULT NULL COMMENT '비용 타입 (regular, tax, adjustment, rounding_error)',
    `currency` varchar(10) DEFAULT NULL COMMENT '통화 코드 (KRW, USD)',
    `currency_conversion_rate` double DEFAULT NULL COMMENT '환율',
    `export_time` datetime DEFAULT NULL COMMENT '데이터 추출 시각',
    `invoice_month` varchar(6) DEFAULT NULL COMMENT '청구월 (YYYYMM)',
    `service_id` varchar(255) DEFAULT NULL COMMENT '서비스 고유 ID',
    `service_description` varchar(255) DEFAULT NULL COMMENT '서비스 명칭',
    `sku_id` varchar(255) DEFAULT NULL COMMENT 'SKU 고유 ID',
    `sku_description` varchar(512) DEFAULT NULL COMMENT 'SKU 상세 명칭',
    `project_id` varchar(255) DEFAULT NULL COMMENT '프로젝트 ID',
    `project_number` varchar(255) DEFAULT NULL COMMENT '프로젝트 번호',
    `project_name` varchar(255) DEFAULT NULL COMMENT '프로젝트 명칭',
    `project_ancestry_numbers` varchar(512) DEFAULT NULL COMMENT '상위 조직 경로',
    `location` varchar(255) DEFAULT NULL COMMENT '상세 위치',
    `location_country` varchar(10) DEFAULT NULL COMMENT '국가 코드',
    `location_region` varchar(255) DEFAULT NULL COMMENT '리전',
    `location_zone` varchar(255) DEFAULT NULL COMMENT '존',
    `usage_start_time` datetime DEFAULT NULL COMMENT '사용 시작 시각',
    `usage_end_time` datetime DEFAULT NULL COMMENT '사용 종료 시각',
    `usage_amount` double DEFAULT NULL COMMENT '사용량',
    `usage_unit` varchar(50) DEFAULT NULL COMMENT '사용량 단위',
    `usage_amount_in_pricing_units` double DEFAULT NULL COMMENT '과금 단위 기준 수량',
    `usage_pricing_unit` varchar(50) DEFAULT NULL COMMENT '과금 단위',
    `adjustment_info_id` varchar(255) DEFAULT NULL COMMENT '조정 ID',
    `adjustment_info_description` varchar(512) DEFAULT NULL COMMENT '조정 설명',
    `adjustment_info_mode` varchar(50) DEFAULT NULL COMMENT '조정 모드',
    `adjustment_info_type` varchar(50) DEFAULT NULL COMMENT '조정 타입',
    `labels` text DEFAULT NULL COMMENT '라벨 (JSON)',
    `system_labels` text DEFAULT NULL COMMENT '시스템 라벨 (JSON)',
    `tags` text DEFAULT NULL COMMENT '태그 (JSON)',
    -- labels(sys.*) 추출 → servicegroup_meta 매핑용 식별자 (gcpCollector ddl_gcp_billing_raw.sql / GCP_SQL.xml initTable 과 동일)
    `csp_instanceid` varchar(200) DEFAULT NULL COMMENT 'Tumblebug 매칭 키 = servicegroup_meta.csp_instanceid (labels.sys_cspresourceid | GKE 클러스터명 | GCS 버킷명)',
    `vm_id` varchar(100) DEFAULT NULL COMMENT 'labels.sys_id',
    `mci_id` varchar(100) DEFAULT NULL COMMENT 'labels.sys_infraid',
    `service_cd` varchar(100) DEFAULT NULL COMMENT 'labels.sys_namespace (ns_id)',
    `k8s_cluster_name` varchar(255) DEFAULT NULL COMMENT 'labels/system_labels.goog-k8s-cluster-name',
    `resource_name` varchar(512) DEFAULT NULL COMMENT 'detailed export resource.name (표준 export 는 NULL)',
    PRIMARY KEY (`id`),
    KEY `idx_billing_date` (`billing_account_id`,`invoice_month`),
    KEY `idx_project_date` (`project_id`,`usage_start_time`),
    KEY `idx_service` (`service_description`,`usage_start_time`),
    KEY `idx_csp_instanceid` (`csp_instanceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci COMMENT='GCP 빌링 원본 데이터';


-- ============================================================
-- provider_keys: LLM 프로바이더 API 키 (AES-256-GCM 암호화 저장)
-- ============================================================

CREATE TABLE IF NOT EXISTS `provider_keys` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ns_id` varchar(100) NOT NULL COMMENT 'namespace ID',
    `provider` varchar(20)  NOT NULL COMMENT 'openai | anthropic | google',
    `enc_key` text NOT NULL COMMENT 'AES-256-GCM 암호문 (base64)',
    `iv` varchar(100) NOT NULL COMMENT 'nonce 12바이트 (base64)',
    `tag` varchar(100) NOT NULL COMMENT 'GCM 인증 태그 16바이트 (base64)',
    `created_at` timestamp DEFAULT current_timestamp(),
    `updated_at` timestamp DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_ns_provider` (`ns_id`, `provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci COMMENT='LLM 프로바이더 API 키 (AES-256-GCM 암호화 저장)';
