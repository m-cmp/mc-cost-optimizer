create table TASM_CLOUD_RGN_M
(
    CLOUD_VNDR_ID  VARCHAR(50)            NOT NULL COMMENT 'CLOUD VENDOR ID',
    RGN_ID         VARCHAR(50)            NOT NULL COMMENT 'REGION ID',
    RGN_NM         VARCHAR(100)           NOT NULL COMMENT 'REGION 명',
    RGN_CODE       VARCHAR(100)           NULL,
    UNIT_PRICE_RGN VARCHAR(100)           NULL,
    MT_RGN_NM      VARCHAR(100)           NULL COMMENT 'REGION 명 (METERING)',
    SVC_TYPE       VARCHAR(10)            NOT NULL COMMENT 'SERVICE TYPE( GLOBAL, CHINA)',
    USE_YN         VARCHAR(1) DEFAULT 'Y' NOT NULL COMMENT 'CLOUD 사용 여부 (''N'':사용안함, ''Y'':사용)',
    CREA_DT        DATETIME               NOT NULL COMMENT '생성일시',
    CREA_ID        VARCHAR(50)            NOT NULL COMMENT '생성자 ID',
    CREA_IPADDR    VARCHAR(39)            NOT NULL COMMENT '생성자 IP주소',
    UPDT_DT        DATETIME               NULL COMMENT '수정일시',
    UPDT_ID        VARCHAR(50)            NULL COMMENT '수정자 ID',
    UPDT_IPADDR    VARCHAR(39)            NULL COMMENT '수정자 IP주소',
    PRIMARY KEY (CLOUD_VNDR_ID, RGN_ID)
)
    COMMENT 'Cloud Region Informations' CHARSET = utf8mb3;

