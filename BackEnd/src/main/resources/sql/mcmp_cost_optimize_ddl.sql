-- ######## mcmp db 작업 쿼리문 ########## --

-- db 생성
create database cost_optimize;

-- dashboard : 대시보드 위젯 관련 ddl --

-- 사용가능한 위젯 type
CREATE TABLE `cost_optimize`.`dashboard_widget_type` (
  `widget_type` varchar(128) NOT NULL,
  PRIMARY KEY (`widget_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 웨젯별 사용가능한 옵션

CREATE TABLE `cost_optimize`.`dashboard_widget_options` (
  `widget_type` varchar(128) NOT NULL,
  `view_by` varchar(128) DEFAULT NULL,
  `date_type` varchar(128) DEFAULT NULL,
  `time_frame` varchar(256) DEFAULT NULL,
  `chart_type` varchar(128) DEFAULT NULL,
  `scale` varchar(128) DEFAULT NULL,
  `filter` varchar(128) DEFAULT NULL,
  `threshold` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`widget_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 위젯 기본 설정

CREATE TABLE `cost_optimize`.`dashboard_widget_default_values` (
  `widget_type` varchar(128) NOT NULL,
  `view_by` varchar(32) DEFAULT NULL,
  `date_type` varchar(32) DEFAULT NULL,
  `time_frame` varchar(64) DEFAULT NULL,
  `chart_type` varchar(32) DEFAULT NULL,
  `scale` varchar(32) DEFAULT NULL,
  `filter` varchar(32) DEFAULT NULL,
  `threshold` int DEFAULT NULL,
  PRIMARY KEY (`widget_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 사용자별 대시보드 보유 리스트

CREATE TABLE `cost_optimize`.`dashboard_user_data` (
  `uuid` binary(16) DEFAULT NULL,
  `site_id` varchar(64) NULL,
  `company_id` varchar(64) NULL,
  `user_id` varchar(128) NULL,
  `dashboard_index` int NOT NULL DEFAULT '0',
  `is_template` boolean DEFAULT NULL,
  `is_dashboard_selected` boolean DEFAULT NULL,
  `dashboard_name` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`dashboard_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 사용자 위젯 설정 데이터
CREATE TABLE `cost_optimize`.`dashboard_widget_user_data` (
  `site_id` varchar(64) default NULL,
  `company_id` varchar(64) default NULL ,
  `user_id` varchar(128) default NULL,
  `dashboard_index` int NOT NULL DEFAULT '0',
  `widget_index` int NOT NULL DEFAULT '0',
  `x` int DEFAULT NULL,
  `y` int DEFAULT NULL,
  `width` int DEFAULT NULL,
  `height` int DEFAULT NULL,
  `widget_type` varchar(128) DEFAULT NULL,
  `date_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `chart_type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `view_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `filter` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `time_frame` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `scale` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `selected_account` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `selected_vendors_by_widget` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `use_yn` varchar(1) DEFAULT 'Y',
  `is_abnormal_noti_on` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`dashboard_index`,`widget_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


-- common : 공통

-- 현재 통화

CREATE TABLE `cost_optimize`.`currencies` (
    currency_code VARCHAR(3) PRIMARY KEY,
    exchange_rate DECIMAL(18, 6),
    exchangeRateDate date
);

-- vendor 이용 상태

CREATE TABLE `cost_optimize`.`vendor_status` (
    siteCd VARCHAR(10),
    mspVndrId VARCHAR(20),
    mspVndrStatus CHAR(1),
    PRIMARY KEY(siteCd, mspVndrId)
);

-- company account
CREATE TABLE `cost_optimize`.`company_vendor_account_list` (
    accId VARCHAR(30)  ,
    alias VARCHAR(50) ,
    hlthYn CHAR(1) ,
    vendor VARCHAR(20)
);
-- company vendor
CREATE TABLE `cost_optimize`.`company_vendor` (
    cloudVndrId VARCHAR(20) PRIMARY KEY,
    cloudVndrNm VARCHAR(50) NOT NULL
);


-- LOGIN : 로그인 정보

-- 로그인 데이터
-- cost_optimize.user_login_info definition

CREATE TABLE `cost_optimize`.`user_login_info` (
  `user_id` varchar(50) NOT NULL,
  `siteCd` varchar(10) DEFAULT NULL,
  `userEmail` varchar(100) DEFAULT NULL,
  `userNm` varchar(50) DEFAULT NULL,
  `userStatCd` varchar(20) DEFAULT NULL,
  `blntCmpnId` varchar(10) DEFAULT NULL,
  `blntCmpnTypeCd` varchar(20) DEFAULT NULL,
  `curCmpnId` varchar(10) DEFAULT NULL,
  `userLangCd` varchar(5) DEFAULT NULL,
  `userPwdUpdtDt` datetime DEFAULT NULL,
  `pwdMustChangeYn` char(1) DEFAULT NULL,
  `chargeType` varchar(20) DEFAULT NULL,
  `useConcurrent` tinyint(1) DEFAULT NULL,
  `currentCustomerOrderd` tinyint(1) DEFAULT NULL,
  `creaDt` datetime DEFAULT NULL,
  `updtDt` datetime DEFAULT NULL,
  `blntCmpnNm` varchar(50) DEFAULT NULL,
  `userStatNm` varchar(50) DEFAULT NULL,
  `blntCmpnTypeNm` varchar(50) DEFAULT NULL,
  `curCmpnNm` varchar(50) DEFAULT NULL,
  `trialYn` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- abnormal : 이상비용

-- 이상 비용
CREATE TABLE `cost_optimize`.`abnormal_data` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `siteId` VARCHAR(50),
    `companyId` VARCHAR(50),
    `userId` VARCHAR(50),
    `userEmail` VARCHAR(50),
    `dashboardIndex` INT,
    `widgetIndex` INT,
    `level` VARCHAR(20),
    `detcBy` VARCHAR(20),
    `vendor` VARCHAR(50),
    `analDt` DATE,
    `cost` DECIMAL(18, 6),
    `rate` DECIMAL(18, 6),
    `sensitivity` VARCHAR(20),
    `detcDt` DATETIME,
    `alarmYn` VARCHAR(5),
    `hideYn` VARCHAR(5),
    `impotYn` VARCHAR(5),
    `creaDt` DATE,
    `minAlert` DECIMAL(18, 6),
    `maxAlert` DECIMAL(18, 6),
    `widgetTitle` VARCHAR(100),
    `dashboardTitle` VARCHAR(100),
    `savedHistory` BOOLEAN
);

-- abnormal_message 테이블 수정
CREATE TABLE `cost_optimize`.`abnormal_message` (
    `id` INT,
    `siteId` VARCHAR(50),
    `companyId` VARCHAR(50),
    `userId` VARCHAR(50),
    `item` VARCHAR(700) COMMENT '
          "421486388841",
          "216093335544",
          "099888603232",
          "338715797467",
          "971924526134",
          "557892227155"
        의 형식으로 string이 들어가게',
    `message_cost` DECIMAL(18, 6),
    `message_rate` DECIMAL(18, 6),
    `message_vendor` VARCHAR(50),
    `message_count` INT,
    `currency` VARCHAR(5),
    `timeFrame` VARCHAR(50),
    `viewBy` VARCHAR(50)
);


-- widget 관련

-- 위젯>abnormal

CREATE TABLE `cost_optimize`.`abnormal_widget_summary` (
    `totalIncreaseDecreaseCost` DECIMAL(18, 6),
    `totalIncreaseDecreaseRate` DECIMAL(5, 2),
    `latestSummarizedBillDate` DATE
);

CREATE TABLE `cost_optimize`.`abnormal_widget_list` (
    `item` VARCHAR(20),
    `itemAlias` VARCHAR(50),
    `currentCost` DECIMAL(18, 6),
    `lastCost` DECIMAL(18, 6),
    `increaseDecreaseRate` DECIMAL(18, 6),
    `costChanges` VARCHAR(50),
    `vendor` VARCHAR(50),
    `alarmLevel` VARCHAR(20),
    `levelStates` VARCHAR(50)
);

-- 위젯 > cost

CREATE TABLE `cost_optimize`.`cost_widget_list` (
    `cost` DECIMAL(18, 6),
    `item` VARCHAR(100),
    `itemAlias` VARCHAR(100),
    `vendor` VARCHAR(50),
    `isOthers` BOOLEAN,
    `orderNum` INT,
    `serviceGroup` BOOLEAN,
    `meterDate` VARCHAR(15) COMMENT '측정 날짜'
);

CREATE TABLE `cost_optimize`.`cost_widget_customFilters` (
    `item` VARCHAR(255),
    `itemAlias` VARCHAR(255),
    `vendor` VARCHAR(50),
    `order` INT,
    `serviceGroup` BOOLEAN
);

-- 위젯 > trend
CREATE TABLE `cost_optimize`.`trend_widget_list`(
    `date` VARCHAR(10),
    `dailyDate` VARCHAR(5),
    `cost` DECIMAL(18, 6)
);

-- 위젯 > top5 원 차트
CREATE TABLE `cost_optimize`.`cost_widget_top5` (
    `cost` DECIMAL(18, 6),
    `item` VARCHAR(100),
    `itemAlias` VARCHAR(100),
    `vendor` VARCHAR(50),
    `isOthers` BOOLEAN,
    `numberOfOthers` INT,
    `order` INT,
    `serviceGroup` BOOLEAN
);

-- 위젯 > productPortion

CREATE TABLE `cost_optimize`.`product_portion_widget_family` (
    `familyCode` VARCHAR(255) PRIMARY KEY,
    `cost` DECIMAL(20, 10),
    `order` INT,
    `serviceGroup` BOOLEAN,
    `numberOfOthers` INT
);

CREATE TABLE `cost_optimize`.`product_portion_widget_item` (
    `familyCode` VARCHAR(255),
    `cost` DECIMAL(20, 10),
    `item` VARCHAR(255),
    `itemAlias` VARCHAR(255),
    `vendor` VARCHAR(255),
    `isOthers` BOOLEAN,
    `order` INT,
    `serviceGroup` BOOLEAN
);

CREATE TABLE `cost_optimize`.`product_portion_widget_timeframe` (
    `time` VARCHAR(10) PRIMARY KEY,
    `isCurrent` BOOLEAN
);


CREATE TABLE `cost_optimize`.`product_portion_widget_account` (
	`account` VARCHAR(100),
    `item` VARCHAR(255),
    `itemAlias` VARCHAR(255),
    `vendor` VARCHAR(255),
    `order` INT,
    `serviceGroup` BOOLEAN
);


-- billing : 빌링 인보이스

-- charge 부분
CREATE TABLE `cost_optimize`.`billing_charge` (
    `linkedAccountId` VARCHAR(255),
    `linkedAccountAlias` VARCHAR(255),
    `vendor` VARCHAR(255),
    `chargeYear` VARCHAR(4),
    `chargeMonth` VARCHAR(2),
    `totalCharge` DECIMAL(18, 6),
    `invoiceCurrency` VARCHAR(10),
    `companyCurrency` VARCHAR(10),
    `cloudCost` DECIMAL(18, 6),
    `cloudOriginalCost` DECIMAL(18, 6),
    `onDemandDiscount` DECIMAL(18, 6),
    `cloudFrontDiscount` DECIMAL(18, 6),
    `cloudFrontDtoDiscount` DECIMAL(18, 6),
    `cloudFrontReqDiscount` DECIMAL(18, 6),
    `cloudServiceCharge` DECIMAL(18, 6),
    `exchangedCloudServiceCharge` DECIMAL(18, 6),
    `salesDiscount` DECIMAL(18, 6),
    `salesDiscountApplyType` VARCHAR(1),
    `salesDiscountApplyValue` DECIMAL(6, 2),
    `supportFee` DECIMAL(18, 6),
    `supportFeeApplyValue` DECIMAL(5, 2),
    `credit` DECIMAL(18, 6)
);

CREATE TABLE `cost_optimize`.`billing_charge_additional_services` (
    `linkedAccountId` VARCHAR(255),
    `linkedAccountAlias` VARCHAR(255),
    `vendor` VARCHAR(255),
    `chargeYear` VARCHAR(4),
    `chargeMonth` VARCHAR(2),
    `additionalServiceName` VARCHAR(255),
    `additionalServiceCode` VARCHAR(255),
    `additionalServiceCharge` DECIMAL(18, 6)
);

-- detail 부분
CREATE TABLE `cost_optimize`.`billing_detail` (
    `linkedAccountId` VARCHAR(20),
    `linkedAccountAlias` VARCHAR(255),
    `productName` VARCHAR(255),
    `regionName` VARCHAR(255),
    `usageType` VARCHAR(255),
    `invoiceId` VARCHAR(20),
    `tagValue` VARCHAR(255),
    `tagKey` VARCHAR(255),
    `usage` DECIMAL(20, 10),
    `cost` DECIMAL(20, 10),
    `itemDescription` VARCHAR(255),
    `serviceGroup` VARCHAR(255),
    `domainName` VARCHAR(255),
    `invoiceType` VARCHAR(255)
);

-- bills 부분

CREATE TABLE `cost_optimize`.`billing_info` (
  `chargeYear` varchar(4) DEFAULT NULL,
  `chargeMonth` varchar(2) DEFAULT NULL,
  `startDate` datetime DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `increaseDecreaseRate` double DEFAULT NULL,
  `totalCharge` double DEFAULT NULL,
  `invoiceCurrency` varchar(3) DEFAULT NULL,
  `companyCurrency` varchar(3) DEFAULT NULL,
  `applyExchangeRate` double DEFAULT NULL,
  `applyExchangeRateDate` date DEFAULT NULL,
  `cloudCost` double DEFAULT NULL,
  `cloudOriginalCost` double DEFAULT NULL,
  `cloudUseOriginalCost` double DEFAULT NULL,
  `onDemandDiscount` double DEFAULT NULL,
  `cloudFrontDiscount` double DEFAULT NULL,
  `supportFee` double DEFAULT NULL,
  `salesDiscount` double DEFAULT NULL,
  `credit` double DEFAULT NULL,
  `cloudServiceCharge` double DEFAULT NULL,
  `exchangedCloudServiceCharge` double DEFAULT NULL,
  `additionalServiceCharge` double DEFAULT NULL,
  `vatYn` char(1) DEFAULT NULL,
  `billConfirmationYn` char(1) DEFAULT NULL,
  `lastBillUpdateDate` date DEFAULT NULL,
  `billingInfoId` int DEFAULT NULL
);

CREATE TABLE `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId` INT,
    `additionalServiceName` VARCHAR(255),
    `additionalServiceCode` VARCHAR(10),
    `additionalServiceCharge` DOUBLE
);





