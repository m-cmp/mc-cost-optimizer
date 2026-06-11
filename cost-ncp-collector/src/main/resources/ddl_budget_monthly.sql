create table budget_monthly
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
