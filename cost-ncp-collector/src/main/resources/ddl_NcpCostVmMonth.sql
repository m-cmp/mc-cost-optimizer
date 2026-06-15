CREATE TABLE ncp_cost_vm_month
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
    demand_amount             DOUBLE                NOT NULL COMMENT '청구 금액. ex) 29520',
    write_date                datetime              NOT NULL COMMENT '작성 일시(YYYY-MM-DDThh:mm:ssZ). ex) 2025-09-11 08:02:40+0900',
    pay_currency              VARCHAR(255)          NOT NULL COMMENT '결제 통화. ex) KRW',
    CONSTRAINT pk_ncp_cost_vm_month PRIMARY KEY (id) COMMENT         'VM별 청구 비용 목록'
);