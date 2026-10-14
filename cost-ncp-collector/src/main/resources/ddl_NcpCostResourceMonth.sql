-- NCP 자원(K8S / OBJECT_STORAGE) 계약별 월 누적 청구 비용 — ncp_cost_vm_month 자매 테이블 (server_spec_code 없음)
--   resource_id = servicegroup_meta.csp_instanceid 조인 키 (K8S = NKS 클러스터 UUID, OBJECT_STORAGE = member_no[계정 단위])
--   UNIQUE 키가 재실행 dedupe 의 실체 (snapshot_date = DATE(write_date), 하루 1 스냅샷/키). INSERT IGNORE 와 함께 사용.
--   공유 DDL(mysql/init_cost_db_ddl.sql) 의 동일 블록과 내용을 맞출 것.
CREATE TABLE IF NOT EXISTS ncp_cost_resource_month
(
    id                        BIGINT AUTO_INCREMENT NOT NULL COMMENT '아이디',
    created                   datetime              NULL     COMMENT 'row insert 시간',
    updated                   datetime              NULL     COMMENT 'row update 시간',
    member_no                 VARCHAR(100)          NOT NULL COMMENT '회원 번호. ex) 0000000',
    demand_month              VARCHAR(6)            NOT NULL COMMENT '청구 월. ex) 202510',
    region_code               VARCHAR(10)           NOT NULL DEFAULT 'KR' COMMENT '리전 코드. ex) KR',
    resource_type             VARCHAR(20)           NOT NULL COMMENT '자원 유형. K8S | OBJECT_STORAGE',
    demand_type_code          VARCHAR(20)           NOT NULL COMMENT '청구 유형 코드. ex) OSSM',
    demand_type_name          VARCHAR(100)          NULL     COMMENT '청구 유형 이름. ex) Object Storage',
    demand_type_detail_code   VARCHAR(20)           NOT NULL DEFAULT '' COMMENT '청구 유형 상세 코드',
    demand_type_detail_name   VARCHAR(100)          NULL     COMMENT '청구 유형 상세 이름',
    contract_no               VARCHAR(50)           NOT NULL DEFAULT '' COMMENT '계약 번호',
    product_code              VARCHAR(50)           NULL     COMMENT '상품 코드 (contractProductList[0].productCode)',
    product_name              VARCHAR(200)          NULL     COMMENT '상품 이름 (contractProductList[0].productItemKind.codeName)',
    instance_no               VARCHAR(100)          NULL     COMMENT 'NCP 인스턴스 번호(원본). ex) 23320000',
    instance_name             VARCHAR(200)          NULL     COMMENT 'NCP 인스턴스 이름(원본, contract.instanceName)',
    resource_id               VARCHAR(200)          NOT NULL COMMENT 'servicegroup_meta.csp_instanceid 조인 키. K8S=NKS 클러스터 UUID, OBJECT_STORAGE=member_no(계정단위)',
    resource_name             VARCHAR(200)          NULL     COMMENT '자원 표시 이름. K8S=클러스터명, OBJECT_STORAGE=instance_name',
    resource_id_source        VARCHAR(20)           NOT NULL COMMENT 'resource_id 출처. NKS_UUID | INSTANCE_NO | CONTRACT_NO | MEMBER_NO',
    usage_unit_code           VARCHAR(50)           NOT NULL DEFAULT '' COMMENT '사용량 단위 코드. ex) USAGE_HH',
    usage_unit_name           VARCHAR(100)          NOT NULL DEFAULT '' COMMENT '사용량 단위 이름',
    product_price             DOUBLE                NOT NULL DEFAULT 0 COMMENT '상품 가격',
    unit_usage_quantity       DOUBLE                NOT NULL DEFAULT 0 COMMENT '단위 사용량',
    total_unit_usage_quantity DOUBLE                NOT NULL DEFAULT 0 COMMENT '총 단위 사용량',
    use_amount                DOUBLE                NOT NULL COMMENT '사용 금액(월 누적, KRW)',
    demand_amount             DOUBLE                NOT NULL COMMENT '청구 금액(월 누적, KRW)',
    write_date                datetime              NOT NULL COMMENT 'NCP 작성 일시. ex) 2025-09-11 08:02:40',
    snapshot_date             DATE                  NOT NULL COMMENT 'DATE(write_date). 재실행 dedupe 키',
    pay_currency              VARCHAR(10)           NOT NULL COMMENT '결제 통화. ex) KRW',
    CONSTRAINT pk_ncp_cost_resource_month PRIMARY KEY (id) COMMENT 'NCP 자원(K8S/ObjectStorage)별 월 누적 청구 비용',
    CONSTRAINT uk_ncp_cost_resource_month UNIQUE (member_no, demand_month, resource_type, resource_id, region_code, demand_type_detail_code, contract_no, snapshot_date),
    INDEX idx_ncr_month_type_rid (resource_type, resource_id, demand_month),
    INDEX idx_ncr_month_month_rid (demand_month, resource_id, id),
    INDEX idx_ncr_month_write (write_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
