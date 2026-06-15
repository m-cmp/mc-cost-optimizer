CREATE TABLE TASM_AZR_VM_PRICING_L
(
    SKU                          VARCHAR(150) NOT NULL,
    REGION                       VARCHAR(50)  NOT NULL,
    INSTANCE_TYPE                VARCHAR(50)  NULL,
    CORES                        DOUBLE       NULL,
    RAM                          DOUBLE       NULL,
    SERIES                       VARCHAR(50)  NULL,
    OS                           VARCHAR(50)  NULL,
    PER_HOUR                     DOUBLE       NULL,
    PER_HOUR_ONE_YEAR_RESERVED   DOUBLE       NULL,
    PER_HOUR_THREE_YEAR_RESERVED DOUBLE       NULL,
    PER_HOUR_SPOT                DOUBLE       NULL,
    IS_VCPU                      VARCHAR(50)  NULL,
    AVAILABLE_FOR_ML             VARCHAR(50)  NULL,
    IS_HIDDEN                    VARCHAR(50)  NULL,
    DISK_SIZE                    DOUBLE       NULL,
    GPU                          VARCHAR(50)  NULL,
    PRICING_TYPES                VARCHAR(50)  NULL,
    CREA_DT                      DATETIME     NULL COMMENT '생성일시',
    PRIMARY KEY (SKU, REGION)
);

CREATE INDEX IX_TASM_AZR_VM_PRICING_L_02
    ON TASM_AZR_VM_PRICING_L (REGION, INSTANCE_TYPE, OS, PRICING_TYPES);

