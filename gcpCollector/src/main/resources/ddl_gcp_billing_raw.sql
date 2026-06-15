CREATE TABLE IF NOT EXISTS gcp_billing_raw
(
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
    created                         DATETIME        NULL COMMENT '수집 시각',

    -- 기본 필드
    billing_account_id              VARCHAR(255)    NULL COMMENT '청구 계정 ID',
    cost                            DOUBLE          NULL COMMENT '발생 비용',
    cost_type                       VARCHAR(50)     NULL COMMENT '비용 타입 (regular, tax, adjustment, rounding_error)',
    currency                        VARCHAR(10)     NULL COMMENT '통화 코드 (KRW, USD)',
    currency_conversion_rate        DOUBLE          NULL COMMENT '환율',
    export_time                     DATETIME        NULL COMMENT '데이터 추출 시각',

    -- invoice (STRUCT → 평탄화)
    invoice_month                   VARCHAR(6)      NULL COMMENT '청구월 (YYYYMM)',

    -- service (STRUCT → 평탄화)
    service_id                      VARCHAR(255)    NULL COMMENT '서비스 고유 ID',
    service_description             VARCHAR(255)    NULL COMMENT '서비스 명칭 (Compute Engine, BigQuery 등)',

    -- sku (STRUCT → 평탄화)
    sku_id                          VARCHAR(255)    NULL COMMENT 'SKU 고유 ID',
    sku_description                 VARCHAR(512)    NULL COMMENT 'SKU 상세 명칭',

    -- project (STRUCT → 평탄화)
    project_id                      VARCHAR(255)    NULL COMMENT '프로젝트 ID',
    project_number                  VARCHAR(255)    NULL COMMENT '프로젝트 번호',
    project_name                    VARCHAR(255)    NULL COMMENT '프로젝트 명칭',
    project_ancestry_numbers        VARCHAR(512)    NULL COMMENT '상위 조직 경로',

    -- location (STRUCT → 평탄화)
    location                        VARCHAR(255)    NULL COMMENT '상세 위치 (us-west1)',
    location_country                VARCHAR(10)     NULL COMMENT '국가 코드 (US)',
    location_region                 VARCHAR(255)    NULL COMMENT '리전 (us-west1)',
    location_zone                   VARCHAR(255)    NULL COMMENT '존 (us-west1-a)',

    -- usage (STRUCT → 평탄화)
    usage_start_time                DATETIME        NULL COMMENT '사용 시작 시각',
    usage_end_time                  DATETIME        NULL COMMENT '사용 종료 시각',
    usage_amount                    DOUBLE          NULL COMMENT '사용량',
    usage_unit                      VARCHAR(50)     NULL COMMENT '사용량 단위 (seconds, byte-seconds)',
    usage_amount_in_pricing_units   DOUBLE          NULL COMMENT '과금 단위 기준 수량',
    usage_pricing_unit              VARCHAR(50)     NULL COMMENT '과금 단위 (hour, gibibyte month)',

    -- adjustment_info (STRUCT → 평탄화)
    adjustment_info_id              VARCHAR(255)    NULL COMMENT '조정 ID',
    adjustment_info_description     VARCHAR(512)    NULL COMMENT '조정 설명',
    adjustment_info_mode            VARCHAR(50)     NULL COMMENT '조정 모드',
    adjustment_info_type            VARCHAR(50)     NULL COMMENT '조정 타입',

    -- labels, tags (ARRAY → JSON 텍스트)
    labels                          TEXT            NULL COMMENT '라벨 (JSON)',
    system_labels                   TEXT            NULL COMMENT '시스템 라벨 (JSON)',
    tags                            TEXT            NULL COMMENT '태그 (JSON)',

    -- labels(sys.*) 추출 → servicegroup_meta 매핑용 식별자 (컬럼명 servicegroup_meta와 정렬)
    csp_instanceid                  VARCHAR(200)    NULL COMMENT 'labels.sys_cspresourceid (servicegroup_meta.csp_instanceid 조인키)',
    vm_id                           VARCHAR(100)    NULL COMMENT 'labels.sys_id',
    mci_id                          VARCHAR(100)    NULL COMMENT 'labels.sys_infraid',
    service_cd                      VARCHAR(100)    NULL COMMENT 'labels.sys_namespace (ns_id)',

    -- 인덱스
    INDEX idx_billing_date (billing_account_id, invoice_month),
    INDEX idx_project_date (project_id, usage_start_time),
    INDEX idx_service (service_description, usage_start_time),
    INDEX idx_csp_instanceid (csp_instanceid)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GCP 빌링 원본 데이터';

