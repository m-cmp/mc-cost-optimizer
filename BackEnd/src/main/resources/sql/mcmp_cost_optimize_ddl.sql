-- ######## mcmp db 작업 쿼리문 ########## --

-- db 생성
create database cost;

-- 1. cur_origin create
CREATE TABLE `cur_origin` (
                              `lineitem_usageaccountid` varchar(100) NOT NULL COMMENT 'AWS 서브계정 구분 ID',
                              `lineitem_productcode` varchar(100) NOT NULL COMMENT '서비스구분코드',
                              `lineitem_resourceid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '인스턴스 구분 코드',
                              `lineitem_lineitemtype` varchar(100) DEFAULT NULL COMMENT '비용구분',
                              `product_instancetype` varchar(100) DEFAULT NULL COMMENT '서비스 상세 구분',
                              `pricing_unit` varchar(100) DEFAULT NULL COMMENT '사용량 단위',
                              `lineitem_usageamount` varchar(100) DEFAULT NULL COMMENT '사용량',
                              `lineitem_unblendedcost` varchar(100) DEFAULT NULL COMMENT 'on-demand 비용',
                              `lineitem_blendedcost` varchar(100) DEFAULT NULL COMMENT '할인 청구 비용',
                              `lineitem_usagestartdate` timestamp NULL DEFAULT NULL COMMENT '인스턴스 사용 시작 일시',
                              `lineitem_usageenddate` timestamp NULL DEFAULT NULL COMMENT '인스턴스 사용 끝 일시',
                              `pricing_publicondemandcost` varchar(100) DEFAULT NULL COMMENT 'on-demand 총비용',
                              `pricing_publicondemandrate` varchar(100) DEFAULT NULL COMMENT 'on-demand 단가',
                              `lineitem_currencycode` varchar(20) DEFAULT NULL COMMENT '화폐단위'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. service_meta
CREATE TABLE `service_category` (
                                    `service_cd` varchar(100) NOT NULL COMMENT '서비스 코드',
                                    `service_nm` varchar(100) NOT NULL COMMENT '서비스 명',
                                    `service_type` varchar(100) NOT NULL COMMENT '서비스 타입',
                                    PRIMARY KEY (`service_cd`,`service_nm`,`service_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. servicegroup_meta
CREATE TABLE `servicegroup_meta` (
                                     `csp_type` varchar(100) NOT NULL COMMENT 'CSP 종류',
                                     `csp_account` varchar(100) NOT NULL COMMENT 'CSP 계정 ID',
                                     `csp_instanceid` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'common' COMMENT ' 인스턴스 구분코드',
                                     `service_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '서비스 코드',
                                     `service_nm` varchar(100) DEFAULT NULL COMMENT '서비스 명',
                                     `service_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'project' COMMENT '서비스 타입',
                                     `workspace_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'WorkSpace 코드',
                                     PRIMARY KEY (`csp_type`,`workspace_cd`,`service_cd`,`csp_instanceid`,`csp_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. workspace_meta
CREATE TABLE `workspace_meta` (
                                  `workspace_cd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'WorkSpace 코드',
                                  `workspace_nm` varchar(100) DEFAULT NULL COMMENT 'WorkSpace 이름',
                                  `csp_type` varchar(100) NOT NULL COMMENT 'CSP 유형',
                                  `csp_account` varchar(100) NOT NULL COMMENT 'CSP 계정',
                                  PRIMARY KEY (`workspace_cd`,`csp_type`,`csp_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--if you want to test alarm, try this
--alarmService ddl

-- 1. mailing  (db)

CREATE TABLE `mail_info` (
                             `mail_username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'G메일 username',
                             `mail_password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'G메일 password',
                             `index_cn` int NOT NULL DEFAULT '0',
                             PRIMARY KEY (`index_cn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. slack_test  (db)

CREATE TABLE `slack_token` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `userId` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `OAuthToken` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `channelId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `userId` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
