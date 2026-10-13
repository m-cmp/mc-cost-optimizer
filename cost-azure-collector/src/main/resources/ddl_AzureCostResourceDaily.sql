-- Azure 비-VM 자원(AKS managed cluster, Storage Account) 별 일별 요금 — azure_cost_vm_daily 자매 테이블 (VM 전용 컬럼 없음)
--   resource_id 는 Cost Management 반환값(소문자) 원문 그대로 저장. BackEnd 는 LOWER(servicegroup_meta.csp_instanceid) 와 비교.
--   UNIQUE (subscription_id, resource_id, usage_date) + writer 의 DELETE→INSERT 로 재수집 멱등.
--   공유 DDL(mysql/init_cost_db_ddl.sql) 의 동일 블록과 내용을 맞출 것.
CREATE TABLE IF NOT EXISTS azure_cost_resource_daily
(
    id                  BIGINT AUTO_INCREMENT NOT NULL COMMENT '아이디',
    created             datetime              NULL COMMENT 'row insert 시간',
    updated             datetime              NULL COMMENT 'row update 시간',
    tenant_id           VARCHAR(255)          NOT NULL COMMENT '테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000',
    subscription_id     VARCHAR(36)           NOT NULL COMMENT 'subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000',
    pre_tax_cost        DOUBLE                NOT NULL COMMENT '비용(자원·일 합계). ex) 16345.824',
    usage_date          VARCHAR(8)            NOT NULL COMMENT '날짜(yyyyMMdd). ex) 20250903',
    resource_group_name VARCHAR(255)          NOT NULL COMMENT '리소스 그룹. ex) rg-dongwoo-1',
    resource_id         VARCHAR(512)          NOT NULL COMMENT 'ARM 리소스 아이디(소문자 원문). ex) /subscriptions/.../resourcegroups/rg-1/providers/microsoft.containerservice/managedclusters/aks-1',
    resource_name       VARCHAR(255)          NOT NULL COMMENT 'resource_id 마지막 세그먼트(클러스터명/스토리지계정명). ex) aks-1',
    resource_type       VARCHAR(20)           NOT NULL COMMENT '자원 유형. K8S | K8S_NODE | OBJECT_STORAGE',
    service_name        VARCHAR(255)          NOT NULL COMMENT 'Azure 서비스명. ex) Azure Kubernetes Service, Storage',
    region              VARCHAR(255)          NULL     COMMENT 'ResourceLocation. ex) koreacentral',
    currency            VARCHAR(255)          NOT NULL COMMENT '통화 단위. ex) KRW',
    CONSTRAINT pk_azure_cost_resource_daily PRIMARY KEY (id) COMMENT 'Azure 비-VM 자원(AKS 클러스터, Storage Account) 별 일별 요금 목록',
    CONSTRAINT uk_azure_cost_resource_daily UNIQUE (subscription_id, resource_id, usage_date),
    INDEX idx_acr_date_type (usage_date, resource_type),
    INDEX idx_acr_resource_id (resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
