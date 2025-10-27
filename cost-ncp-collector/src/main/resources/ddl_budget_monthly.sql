create table budget_monthly
(
    id         bigint auto_increment primary key,
    csp        varchar(50)                                not null,
    year       int                                        not null,
    month      int                                        not null,
    budget     decimal(18, 3) default 0.000               null,
    currency   varchar(10)    default 'USD'               not null,
    created_at timestamp      default current_timestamp() null,
    updated_at timestamp      default current_timestamp() null on update current_timestamp(),
    constraint uq_csp_year_month unique (csp, year, month)
);

