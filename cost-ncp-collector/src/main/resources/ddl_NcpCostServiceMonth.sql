CREATE TABLE ncp_cost_service_month
(
    id                  BIGINT AUTO_INCREMENT NOT NULL COMMENT '아이디',
    created             datetime              NULL COMMENT     'row insert 시간',
    updated             datetime              NULL COMMENT     'row update 시간',
    member_no           VARCHAR(255)          NOT NULL COMMENT '회원 번호. ex) 0000000',
    product_demand_type VARCHAR(255)          NOT NULL COMMENT '상품 청구 유형 이름. ex) Virtual Private Cloud',
    demand_month        VARCHAR(255)          NOT NULL COMMENT '청구 월. ex) 202509',
    use_amount          DOUBLE                NOT NULL COMMENT '사용 금액. ex) 2580.0',
    demand_amount       DOUBLE                NOT NULL COMMENT '청구 금액. ex) 2580.0',
    write_date          datetime              NOT NULL COMMENT '작성 일시(YYYY-MM-DDThh:mm:ssZ). ex) 2025-09-11 08:02:40+0900',
    pay_currency        VARCHAR(255)          NOT NULL COMMENT '결제 통화. ex) KRW',
    CONSTRAINT pk_ncp_cost_service_month PRIMARY KEY (id) COMMENT '서비스별 청구 비용 목록'
);