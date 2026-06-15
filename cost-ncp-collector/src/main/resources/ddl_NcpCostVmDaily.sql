-- 1. 테이블 생성 DDL
CREATE TABLE ncp_cost_vm_daily
(
    id                        BIGINT AUTO_INCREMENT NOT NULL COMMENT '아이디',
    created                   datetime              NULL COMMENT     'row insert 시간',
    updated                   datetime              NULL COMMENT     'row update 시간',
    member_no                 VARCHAR(255)          NOT NULL COMMENT '회원 번호. ex) 0000000',
    demand_month              VARCHAR(255)          NOT NULL COMMENT '청구 월. ex) 202509',
    region_code               VARCHAR(255)          NOT NULL COMMENT '리전 코드. ex) KR',
    server_spec_code          VARCHAR(255)          NOT NULL COMMENT '서버 스펙 코드. ex) s2-g2-s50',
    instance_no               VARCHAR(255)          NOT NULL COMMENT '인스턴스 번호. ex) 00000000',
    instance_name             VARCHAR(255)          NOT NULL COMMENT '인스턴스 이름. ex) dongwoo-abc-abc-abc',
    usage_unit_code           VARCHAR(255)          NOT NULL COMMENT '사용량 단위 코드. ex) USAGE_HH',
    usage_unit_name           VARCHAR(255)          NOT NULL COMMENT '사용량 단위 이름. ex) Usage time (per hour)',
    product_price             DOUBLE                NOT NULL COMMENT '상품 가격. ex) 123',
    unit_usage_quantity       DOUBLE                NOT NULL COMMENT '단위 사용량. ex) 240',
    total_unit_usage_quantity DOUBLE                NOT NULL COMMENT '총 단위 사용량. ex) 240',
    use_amount                DOUBLE                NOT NULL COMMENT '사용 금액. ex) 29520',
    demand_amount             DOUBLE                NOT NULL COMMENT '청구 금액(누적). ex) 29520',
    write_date                datetime              NOT NULL COMMENT '작성 일시(YYYY-MM-DDThh:mm:ssZ). ex) 2025-09-11 08:02:40+0900',
    pay_currency              VARCHAR(255)          NOT NULL COMMENT '결제 통화. ex) KRW',
    target_date               DATE                  NOT NULL COMMENT '대상 날짜. ex) 2025-10-03',
    daily_charge_amount       DOUBLE                NOT NULL COMMENT '일일 청구 금액. ex) 5000',
    CONSTRAINT pk_ncp_cost_vm_daily PRIMARY KEY (id) COMMENT 'VM별 일일 청구 비용 목록'
);

-- 인덱스 생성 (조회 성능 향상)
CREATE INDEX idx_instance_target_date ON ncp_cost_vm_daily(instance_no, target_date);
CREATE INDEX idx_target_date ON ncp_cost_vm_daily(target_date);