create table TASM_NCP_INSTANCE_PRICING_L
(
    SKU            varchar(150) not null,
    REGION         varchar(50)  not null,
    INSTANCE_TYPE  varchar(50)  null,
    SERIES         varchar(50)  null,
    PRODUCT_NAME   varchar(150) null,
    PRODUCT_CODE   varchar(100) null,
    CORES          double       null,
    MEMORY         double       null,
    DISK_SIZE      double       null,
    GPU            double       null,
    UNIT_CODE      varchar(50)  not null,
    UNIT_CODE_NAME varchar(150) not null,
    USD            double       null,
    CREA_DT        datetime     null comment '생성일시',
    primary key (SKU, REGION, UNIT_CODE)
);

