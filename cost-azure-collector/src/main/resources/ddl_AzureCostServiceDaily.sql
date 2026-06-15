CREATE TABLE azure_cost_service_daily
(
    id              BIGINT AUTO_INCREMENT NOT NULL comment '아이디',
    created         datetime              NULL COMMENT 'row insert 시간',
    updated         datetime              NULL COMMENT 'row update 시간',
    tenant_id       VARCHAR(255)          NOT NULL COMMENT      '테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000',
    subscription_id VARCHAR(255)          NOT NULL COMMENT      'subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000',
    pre_tax_cost    DOUBLE                NOT NULL COMMENT '비용. ex) 16345.824',
    usage_date      VARCHAR(255)          NOT NULL COMMENT '날짜. ex) 20250903',
    service_name    VARCHAR(255)          NOT NULL COMMENT '서비스 이름. ex) Virtual Machines',
    currency        VARCHAR(255)          NOT NULL COMMENT '통화 단위. ex) KRW',
    CONSTRAINT pk_azure_cost_service_daily PRIMARY KEY (id) COMMENT 'Azure Service 일별 요금을 목록'
);