
-- 1. dashboards, widget, login

-- dashboard_widget_type insert
INSERT INTO `cost_optimize`.`dashboard_widget_type` (`widget_type`)
VALUES
    ('dashboard_abnormal_change_widget'),
    ('dashboard_compare_cost_trend_widget'),
    ('dashboard_cost_by_widget'),
    ('dashboard_cost_month_to_date_widget'),
    ('dashboard_estimated_cost_widget'),
    ('dashboard_product_portion_by_widget'),
    ('dashboard_top_5_widget');

-- dashboard_widget_options
INSERT INTO `cost_optimize`.`dashboard_widget_options` (widget_type,view_by,date_type,time_frame,chart_type,`scale`,`filter`,threshold)
VALUES
    ('dashboard_abnormal_charge_widget','account||product||region',NULL,'compare_last_3_days||compare_last_7_days||compare_latest_day_vs_average_of_7_days||compare_this_month_vs_last_month',NULL,NULL,NULL,'5||10||15'),
    ('dashboard_compare_cost_trend_widget',NULL,NULL,'last_2_months||last_3_months||last_6_months',NULL,NULL,NULL,NULL),
    ('dashboard_cost_by_widget','account||product||region','monthly||weekly','last_12_months||last_6_months||last_3_months||year_to_month,last_24_weeks||last_16_weeks||last_8_weeks||last_4_weeks||year_to_week','stack||line','value||percentage','top_10_by_cost||custom',NULL),
    ('dashboard_product_portion_by_widget',NULL,'monthly||weekly',NULL,NULL,'value||percentage',NULL,NULL),
    ('dashboard_top_5_widget','account||product||region',NULL,'last_60_days||last_30_days||last_14_days||last_10_days||last_7_days||month_to_date','pie',NULL,NULL,NULL);


-- dashboard_widget_default_values
INSERT INTO `cost_optimize`.`dashboard_widget_default_values` (`widget_type`, `view_by`, `date_type`, `time_frame`, `chart_type`, `scale`, `filter`, `threshold`)
VALUES
    ('dashboard_abnormal_charge_widget', 'account', NULL, 'compare_this_month_vs_last_month', NULL, NULL, NULL, 10),
    ('dashboard_compare_cost_trend_widget', NULL, NULL, 'last_2_months', NULL, NULL, NULL, NULL),
    ('dashboard_cost_by_widget', 'account', 'monthly', 'last_12_months', 'stack', 'value', 'top_10_by_cost', NULL),
    ('dashboard_product_portion_by_widget', NULL, 'monthly', NULL, NULL, 'percentage', NULL, NULL),
    ('dashboard_top_5_widget', 'account', NULL, 'last_14_days', 'pie', NULL, NULL, NULL);

-- dashboard_user_data
INSERT INTO `cost_optimize`.`dashboard_user_data` (`site_id`, `company_id`, `user_id`, `dashboard_index`, `is_template`, `is_dashboard_selected`, `dashboard_name`)
VALUES (NULL, NULL, NULL, 0, false, true, 'MCMP');

-- dashboard_widget_user_data
INSERT INTO `cost_optimize`.`dashboard_widget_user_data` (
    `site_id`,
    `company_id`,
    `user_id`,
    `dashboard_index`,
    `widget_index`,
    `x`,
    `y`,
    `width`,
    `height`,
    `widget_type`,
    `date_type`,
    `chart_type`,
    `view_by`,
    `filter`,
    `time_frame`,
    `scale`,
    `selected_account`,
    `selected_vendors_by_widget`,
    `use_yn`,
    `is_abnormal_noti_on`
) VALUES
      (null,null,null,0,4,0,4,24,13,'dashboard_compare_cost_trend_widget',NULL,NULL,NULL,NULL,'last_2_months',NULL,NULL,'AWS','Y',0),
      (null,null,null,0,6,12,17,12,13,'dashboard_cost_by_widget','monthly','line','account','top_10_by_cost','year_to_month','value','','AWS','Y',0),
      (null,null,null,0,7,12,30,12,13,'dashboard_product_portion_by_widget','monthly',NULL,NULL,NULL,'202305','value','000885530975','AWS','Y',0),
      (null,null,null,0,9,0,30,12,13,'dashboard_abnormal_change_widget',NULL,NULL,'account',NULL,'compare_this_month_vs_last_month',NULL,NULL,'AWS','Y',0),
      (null,null,null,0,10,0,17,12,13,'dashboard_top_5_widget',NULL,'pie','account',NULL,'last_14_days','',NULL,'AWS','Y',0),
      (null,null,null,0,11,0,0,12,4,'dashboard_cost_month_to_date_widget',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'AWS','Y',0),
      (null,null,null,0,12,12,0,12,4,'dashboard_estimated_cost_widget',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'AWS','Y',0);


-- user_login_info
INSERT INTO `cost_optimize`.`user_login_info` (user_id,siteCd,userEmail,userNm,userStatCd,blntCmpnId,blntCmpnTypeCd,curCmpnId,userLangCd,userPwdUpdtDt,pwdMustChangeYn,chargeType,useConcurrent,currentCustomerOrderd,creaDt,updtDt,blntCmpnNm,userStatNm,blntCmpnTypeNm,curCmpnNm,trialYn)
VALUES ('MCMP','MCMP','MCMP','mcmpUser','USER_STAT_040','1','CMPN_TYPE_020','1','ko','2023-05-18 05:28:19','Y','Free',0,1,NULL,NULL,'MCMP','activated','company','MCMP','N');


-- abnormal_widget_summary
INSERT INTO `cost_optimize`.`abnormal_widget_summary` (
    `totalIncreaseDecreaseCost`, `totalIncreaseDecreaseRate`, `latestSummarizedBillDate`
) VALUES (
             7858.139999999999, 19.05, '2023-08-19'
         );

-- abnormal_widget_list
INSERT INTO `cost_optimize`.`abnormal_widget_list` (
    `item`, `itemAlias`, `currentCost`, `lastCost`, `increaseDecreaseRate`,
    `costChanges`, `vendor`, `alarmLevel`, `levelStates`
) VALUES
      ('748665785761', 'Data - DEV', 17.545093470799994, 15.617174184400001, 12.35595390524969,
       NULL, 'AWS', 'Minor', NULL),
      ('216093335544', 'MCMP - PRD', 39198.68277185916, 30765.227172690564, 27.41227678128849,
       NULL, 'AWS', 'Critical', NULL),
      ('099888603232', 'Shield Test1', 240.2240656902998, 6940.041483050399, -96.53863666491836,
       NULL, 'AWS', 'Critical', NULL),
      ('338715797467', 'Fin - MEA1', 4094.4508716369055, 2949.0349747332007, 38.84056791555188,
       NULL, 'AWS', 'Critical', NULL),
      ('971924526134', 'MCMP - COM', 365.76405636469997, 463.62689425100007, -21.10950542458426,
       NULL, 'AWS', 'Minor', NULL),
      ('557892227155', 'MCMP - NOS', 5189.9732218502, 114.94884659600018, 4414.980426272293,
       NULL, 'AWS', 'Critical', NULL);

-- cost_widget_list



-- cost_widget_customFilters
INSERT INTO `cost_optimize`.`cost_widget_customFilters` (
    `item`, `itemAlias`, `vendor`, `order`, `serviceGroup`
) VALUES
      ('AWS Backup', 'AWS Backup', 'AWS', 0, 0),
      ('AWS CloudTrail', 'AWS CloudTrail', 'AWS', 0, 0),
      ('AWS Config', 'AWS Config', 'AWS', 0, 0),
      ('AWS Data Transfer', 'AWS Data Transfer', 'AWS', 0, 0),
      ('AWS Glue', 'AWS Glue', 'AWS', 0, 0),
      ('AWS Key Management Service', 'AWS Key Management Service', 'AWS', 0, 0),
      ('AWS Lambda', 'AWS Lambda', 'AWS', 0, 0),
      ('AWS Secrets Manager', 'AWS Secrets Manager', 'AWS', 0, 0),
      ('AWS Security Hub', 'AWS Security Hub', 'AWS', 0, 0),
      ('AWS Service Catalog', 'AWS Service Catalog', 'AWS', 0, 0),
      ('AWS Step Functions', 'AWS Step Functions', 'AWS', 0, 0),
      ('AWS Systems Manager', 'AWS Systems Manager', 'AWS', 0, 0),
      ('AWS WAF', 'AWS WAF', 'AWS', 0, 0),
      ('AWS X-Ray', 'AWS X-Ray', 'AWS', 0, 0),
      ('Amazon API Gateway', 'Amazon API Gateway', 'AWS', 0, 0),
      ('Amazon Athena', 'Amazon Athena', 'AWS', 0, 0),
      ('Amazon Chime Voice Connector a service sold by AMCS LLC', 'Amazon Chime Voice Connector a service sold by AMCS LLC', 'AWS', 0, 0),
      ('Amazon CloudFront', 'Amazon CloudFront', 'AWS', 0, 0),
      ('Amazon Cognito', 'Amazon Cognito', 'AWS', 0, 0),
      ('Amazon Connect Customer Profiles', 'Amazon Connect Customer Profiles', 'AWS', 0, 0),
      ('Amazon DynamoDB', 'Amazon DynamoDB', 'AWS', 0, 0),
      ('Amazon EC2 Container Registry (ECR)', 'Amazon EC2 Container Registry (ECR)', 'AWS', 0, 0),
      ('Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', 0, 0),
      ('Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', 0, 0),
      ('Amazon Elastic Container Registry Public', 'Amazon Elastic Container Registry Public', 'AWS', 0, 0),
      ('Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', 0, 0),
      ('Amazon Elastic Container Service for Kubernetes', 'Amazon Elastic Container Service for Kubernetes', 'AWS', 0, 0),
      ('Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', 0, 0),
      ('Amazon Elastic MapReduce', 'Amazon Elastic MapReduce', 'AWS', 0, 0),
      ('Amazon GuardDuty', 'Amazon GuardDuty', 'AWS', 0, 0),
      ('Amazon Kinesis Firehose', 'Amazon Kinesis Firehose', 'AWS', 0, 0),
      ('Amazon Lightsail', 'Amazon Lightsail', 'AWS', 0, 0),
      ('Amazon MQ', 'Amazon MQ', 'AWS', 0, 0),
      ('Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', 0, 0),
      ('Amazon Pinpoint', 'Amazon Pinpoint', 'AWS', 0, 0),
      ('Amazon QuickSight', 'Amazon QuickSight', 'AWS', 0, 0),
      ('Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', 0, 0),
      ('Amazon Route 53', 'Amazon Route 53', 'AWS', 0, 0),
      ('Amazon SageMaker', 'Amazon SageMaker', 'AWS', 0, 0),
      ('Amazon Simple Email Service', 'Amazon Simple Email Service', 'AWS', 0, 0),
      ('Amazon Simple Notification Service', 'Amazon Simple Notification Service', 'AWS', 0, 0),
      ('Amazon Simple Queue Service', 'Amazon Simple Queue Service', 'AWS', 0, 0),
      ('Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', 0, 0),
      ('Amazon Virtual Private Cloud', 'Amazon Virtual Private Cloud', 'AWS', 0, 0),
      ('AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', 0, 0),
      ('AmazonWorkMail', 'AmazonWorkMail', 'AWS', 0, 0),
      ('CloudWatch Events', 'CloudWatch Events', 'AWS', 0, 0),
      ('Elastic Load Balancing', 'Elastic Load Balancing', 'AWS', 0, 0),
      ('AWS CodeArtifact', 'AWS CodeArtifact', 'AWS', 0, 0),
      ('AWS Cost Explorer', 'AWS Cost Explorer', 'AWS', 0, 0),
      ('Amazon Registrar', 'Amazon Registrar', 'AWS', 0, 0),
      ('AWS Database Migration Service', 'AWS Database Migration Service', 'AWS', 0, 0),
      ('Amazon Dev Guru', 'Amazon Dev Guru', 'AWS', 0, 0),
      ('Amazon Redshift', 'Amazon Redshift', 'AWS', 0, 0),
      ('CodeBuild', 'CodeBuild', 'AWS', 0, 0),
      ('Amazon AppFlow', 'Amazon AppFlow', 'AWS', 0, 0),
      ('AWS Directory Service', 'AWS Directory Service', 'AWS', 0, 0),
      ('Amazon WorkSpaces', 'Amazon WorkSpaces', 'AWS', 0, 0),
      ('Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', 0, 0),
      ('AWS Compute Optimizer', 'AWS Compute Optimizer', 'AWS', 0, 0),
      ('Amazon DocumentDB (with MongoDB compatibility)', 'Amazon DocumentDB (with MongoDB compatibility)', 'AWS', 0, 0),
      ('Amazon Macie', 'Amazon Macie', 'AWS', 0, 0),
      ('Amazon MemoryDB', 'Amazon MemoryDB', 'AWS', 0, 0),
      ('Amazon Verified Permissions', 'Amazon Verified Permissions', 'AWS', 0, 0),
      ('AWS IoT', 'AWS IoT', 'AWS', 0, 0),
      ('Microsoft Windows Server 2019 Base with Containers Support by Bansir', 'Microsoft Windows Server 2019 Base with Containers Support by Bansir', 'AWS', 0, 0);

INSERT INTO `cost_optimize`.`cost_widget_list`
(`cost`, `item`, `itemAlias`, `vendor`, `isOthers`, `orderNum`, `serviceGroup`, `meterDate`)
VALUES
    -- 202209
    (0.0, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202209'),
    (29176.660518378598, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202209'),
    (1012.3199999999999, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202209'),
    (5654.546878847998, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202209'),
    (6636.8399271706, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202209'),
    (666.6606111272, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202209'),
    (2988.7273804996, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202209'),
    (1701.2044691998994, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202209'),
    (1872.0386861541995, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202209'),
    (119.05317303199999, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202209'),
    (4051.965241274687, 'others', 'others', 'AWS', true, 99, false, '202209'),

    -- 202210
    (0.0, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202210'),
    (29246.487033395, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202210'),
    (866.4532, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202210'),
    (5820.572400587204, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202210'),
    (7286.78331919176, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202210'),
    (700.0072607147, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202210'),
    (3118.0493409406, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202210'),
    (1873.8616933917997, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202210'),
    (2110.3172133364023, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202210'),
    (144.1128745868, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202210'),
    (4174.701276514799, 'others', 'others', 'AWS', true, 99, false, '202210'),

    -- 202211
    (0.0, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202211'),
    (33244.9653876525, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202211'),
    (1115.1935999999998, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202211'),
    (6531.042258163695, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202211'),
    (6931.625371850165, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202211'),
    (666.5272222384, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202211'),
    (2972.0150734716, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202211'),
    (2122.4662008664986, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202211'),
    (2307.8782451386987, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202211'),
    (168.30225592110003, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202211'),
    (5124.246121495797, 'others', 'others', 'AWS', true, 99, false, '202211'),

    -- 202212
    (0.0, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202212'),
    (33542.4806703613, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202212'),
    (1049.2224, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202212'),
    (7005.198381481182, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202212'),
    (7244.319758958452, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202212'),
    (685.9997391963, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202212'),
    (3005.3531454151, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202212'),
    (2343.3910125714992, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202212'),
    (2428.6685659177, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202212'),
    (194.4567418735, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202212'),
    (5748.767785722699, 'others', 'others', 'AWS', true, 99, false, '202212'),

    -- 202301
    (0.0, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202301'),
    (34446.4483734364, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202301'),
    (902.3232000000002, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202301'),
    (12092.012659599364, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202301'),
    (7506.191416251456, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202301'),
    (686.3779999488, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202301'),
    (3154.961856209, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202301'),
    (2540.381749477299, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202301'),
    (2748.5224912865024, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202301'),
    (8738.7567193637, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202301'),
    (5056.006333488999, 'others', 'others', 'AWS', true, 99, false, '202301'),

    -- 202302
    (4974.8160000098005, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202302'),
    (31134.2565342793, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202302'),
    (747.8016, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202302'),
    (7186.172004622414, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202302'),
    (7195.508054828904, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202302'),
    (620.0569452894, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202302'),
    (2797.940354191, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202302'),
    (2622.0136162200997, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202302'),
    (2273.432167086902, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202302'),
    (4254.5372280901, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202302'),
    (4649.589607719388, 'others', 'others', 'AWS', true, 99, false, '202302'),

    -- 202303
    (3747.2645121118, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202303'),
    (34525.158627362, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202303'),
    (902.3232, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202303'),
    (7645.542587817878, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202303'),
    (7868.1245539110405, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202303'),
    (678.7636000248, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202303'),
    (2999.3432031903, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202303'),
    (2921.2409427113002, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202303'),
    (2924.242144039402, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202303'),
    (23.0284664316, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202303'),
    (5632.475107722998, 'others', 'others', 'AWS', true, 99, false, '202303'),

    -- 202304
    (0.0, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202304'),
    (34200.461566521604, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202304'),
    (873.2159999999999, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202304'),
    (7646.467474632193, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202304'),
    (7161.343829715213, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202304'),
    (659.179600056, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202304'),
    (2949.5162457673, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202304'),
    (3122.960699940999, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202304'),
    (2672.9545914420996, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202304'),
    (22.965785289800003, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202304'),
    (5286.364951783693, 'others', 'others', 'AWS', true, 99, false, '202304'),

    -- 202305
    (134720.2180303515, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202305'),
    (56105.855388245, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202305'),
    (21912.40088, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202305'),
    (8761.333155029404, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202305'),
    (7650.211709610052, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202305'),
    (9333.6354450248, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202305'),
    (3283.7729455961, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202305'),
    (3298.755087181101, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202305'),
    (3381.6726541057997, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202305'),
    (22.919945131, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202305'),
    (10438.893084622607, 'others', 'others', 'AWS', true, 99, false, '202305'),

    -- 202306
    (126720.3172475879, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202306'),
    (60063.9215768743, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202306'),
    (21478.753254, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202306'),
    (9381.3493843144, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202306'),
    (7838.049482349498, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202306'),
    (9491.006150101199, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202306'),
    (3380.8117486422, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202306'),
    (3235.418692192401, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202306'),
    (3511.796238217307, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202306'),
    (25.4388311018, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202306'),
    (11035.423492457096, 'others', 'others', 'AWS', true, 99, false, '202306'),

    -- 202307
    (130944.0000000092, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202307'),
    (65378.64353964661, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202307'),
    (22303.504, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202307'),
    (11377.34441365492, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202307'),
    (8054.198841631336, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202307'),
    (9449.4088820872, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202307'),
    (3500.7302586734004, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202307'),
    (3502.4889482991002, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202307'),
    (2752.8816981949017, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202307'),
    (9504.9994778016, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202307'),
    (11728.429412398902, 'others', 'others', 'AWS', true, 99, false, '202307'),

    -- 202308
    (134532.2750000063, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, '202308'),
    (58768.471433887105, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, '202308'),
    (21972.448800000002, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, '202308'),
    (5987.008632321093, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, '202308'),
    (5866.0289913937895, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, '202308'),
    (9470.3182059701, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, '202308'),
    (2110.6942435374003, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, '202308'),
    (1981.6709016573002, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, '202308'),
    (2054.349599171699, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, '202308'),
    (17.1280497681, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, '202308'),
    (10942.925553330802, 'others', 'others', 'AWS', true, 99, false, '202308'),
    -- estimated
    (147858.32086588998, 'Savings Plans for AWS Compute usage', 'Savings Plans for AWS Compute usage', 'AWS', false, 1, false, 'estimated'),
    (64589.76112660819, 'Amazon Relational Database Service', 'Amazon Relational Database Service', 'AWS', false, 2, false, 'estimated'),
    (24148.921772709095, 'Amazon ElastiCache', 'Amazon ElastiCache', 'AWS', false, 3, false, 'estimated'),
    (6580.049608055344, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 4, false, 'estimated'),
    (6447.086372530869, 'Amazon Elastic Container Service', 'Amazon Elastic Container Service', 'AWS', false, 5, false, 'estimated'),
    (10408.397152284393, 'Amazon OpenSearch Service', 'Amazon OpenSearch Service', 'AWS', false, 6, false, 'estimated'),
    (2319.7682988020924, 'Amazon MQ', 'Amazon MQ', 'AWS', false, 7, false, 'estimated'),
    (2177.964596434788, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', false, 8, false, 'estimated'),
    (2257.842456057681, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 9, false, 'estimated'),
    (18.82466254598418, 'Amazon Elastic File System', 'Amazon Elastic File System', 'AWS', false, 10, false, 'estimated'),
    (12026.87308808133, 'others', 'others', 'AWS', true, 99, false, 'estimated');




-- 위젯 > trend
INSERT INTO `cost_optimize`.`trend_widget_list` (`date`, `dailyDate`, `cost`)
VALUES
    ('202307', 'd01', 63059.19118929784),
    ('202307', 'd02', 6757.836320771796),
    ('202307', 'd03', 6708.055192821194),
    ('202307', 'd04', 6758.279491264794),
    ('202307', 'd05', 6852.8288725196),
    ('202307', 'd06', 6581.086671718495),
    ('202307', 'd07', 6966.102333215796),
    ('202307', 'd08', 6602.3398200169),
    ('202307', 'd09', 6631.3300319344),
    ('202307', 'd10', 6569.666090809698),
    ('202307', 'd11', 6853.448337042895),
    ('202307', 'd12', 6827.650861243502),
    ('202307', 'd13', 7447.914232878198),
    ('202307', 'd14', 7722.157109623896),
    ('202307', 'd15', 7278.287891776698),
    ('202307', 'd16', 7273.500486956099),
    ('202307', 'd17', 7232.410119351896),
    ('202307', 'd18', 7341.1274859646),
    ('202307', 'd19', 7749.078191876004),
    ('202307', 'd20', 7879.225977935696),
    ('202307', 'd21', 7762.942001839396),
    ('202307', 'd22', 7500.5481399324),
    ('202307', 'd23', 7571.5529723537),
    ('202307', 'd24', 7872.705900367203),
    ('202307', 'd25', 7659.225333028399),
    ('202307', 'd26', 7657.587491970697),
    ('202307', 'd27', 7377.2446399732),
    ('202307', 'd28', 6907.203683473896),
    ('202307', 'd29', 6993.646253884698),
    ('202307', 'd30', 6899.082573164301),
    ('202307', 'd31', 7203.373773389596),
    ('202308', 'd01', 69197.94153962677),
    ('202308', 'd02', 7207.737944838112),
    ('202308', 'd03', 7239.25009722341),
    ('202308', 'd04', 7085.000213749808),
    ('202308', 'd05', 7591.470121759812),
    ('202308', 'd06', 9599.538011121414),
    ('202308', 'd07', 6779.030612451385),
    ('202308', 'd08', 6996.082472458308),
    ('202308', 'd09', 6881.277473275609),
    ('202308', 'd10', 6967.481339167009),
    ('202308', 'd11', 6665.665010509213),
    ('202308', 'd12', 6949.234941825106),
    ('202308', 'd13', 6889.60452713891),
    ('202308', 'd14', 6775.686420211286),
    ('202308', 'd15', 6757.318138590611),
    ('202308', 'd16', 6955.641246200006),
    ('202308', 'd17', 6893.323868419511),
    ('202308', 'd18', 6887.430176601898),
    ('202308', 'd19', 6589.533530936994),
    ('202308', 'd20', 6761.7916876600975),
    ('202308', 'd21', 4680.8800372785),
    ('202308', 'd22', 4680.84),
    ('202308', 'd23', 4568.84),
    ('202308', 'd24', 4512.84),
    ('202308', 'd25', 4512.84),
    ('202308', 'd26', 4512.84),
    ('202308', 'd27', 4512.84),
    ('202308', 'd28', 4512.84),
    ('202308', 'd29', 4512.84),
    ('202308', 'd30', 4512.84),
    ('202308', 'd31', 4512.84);



-- 위젯 > top5 원 차트
INSERT INTO `cost_optimize`.`cost_widget_top5`
(`cost`, `item`, `itemAlias`, `vendor`, `isOthers`, `numberOfOthers`, `order`, `serviceGroup`)
VALUES
    (183570.86395329036, '875531359846', 'Memory DB 사용자', 'AWS', false, NULL, 1, false),
    (58541.400496995746, '216093335544', 'MCMP - PRD', 'AWS', false, NULL, 2, false),
    (9102.602031606999, '370166107047', 'dsfd', 'AWS', false, NULL, 3, false),
    (6077.286439557804, '338715797467', 'Fin - MEA1', 'AWS', false, NULL, 4, false),
    (5667.6818649724955, '557892227155', 'MCMP - NOS', 'AWS', false, NULL, 5, false),
    (11532.707246225898, 'others', 'others', 'AWS', true, 11, 6, false);


-- 위젯 > productPortion
-- product_portion_widget_family 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`product_portion_widget_family` (`familyCode`, `cost`, `order`, `serviceGroup`, `numberOfOthers`)
VALUES
    ('Compute', 12.1314314295, 0, false, NULL),
    ('others', 1.1055909051000001, 0, false, 4),
    ('Management & Governance', 0.1512397187, 0, false, NULL),
    ('Security, Identity, & Compliance', 0.0782136534, 0, false, NULL),
    ('Networking & Content Delivery', 0.0355161425, 0, false, NULL);

-- product_portion_widget_item 테이블에 데이터 삽입

INSERT INTO `cost_optimize`.`product_portion_widget_item` (`familyCode`, `cost`, `item`, `itemAlias`, `vendor`, `isOthers`, `order`, `serviceGroup`)
VALUES
    ('Compute', 12.1314314295, 'Amazon Elastic Compute Cloud', 'Amazon Elastic Compute Cloud', 'AWS', false, 0, false),
    ('Compute', 0.0, 'AWS Lambda', 'AWS Lambda', 'AWS', false, 0, false),
    ('others', 0.41991646880000005, 'AWS Data Transfer', 'AWS Data Transfer', 'AWS', false, 0, false),
    ('others', 0.68, 'AWS Security Hub', 'AWS Security Hub', 'AWS', false, 0, false),
    ('others', 0.005666436299999999, 'Amazon Simple Storage Service', 'Amazon Simple Storage Service', 'AWS', true, 0, false),
    ('others', 0.0, 'Amazon Simple Notification Service', 'Amazon Simple Notification Service', 'AWS', true, 0, false),
    ('others', 8.0E-6, 'Amazon Simple Queue Service', 'Amazon Simple Queue Service', 'AWS', true, 0, false),
    ('others', 0.0, 'AWS Glue', 'AWS Glue', 'AWS', true, 0, false),
    ('Management & Governance', 0.1282272187, 'AmazonCloudWatch', 'AmazonCloudWatch', 'AWS', false, 0, false),
    ('Management & Governance', 0.0230125, 'AWS CloudTrail', 'AWS CloudTrail', 'AWS', false, 0, false),
    ('Security, Identity, & Compliance', 0.0772736534, 'Amazon GuardDuty', 'Amazon GuardDuty', 'AWS', false, 0, false),
    ('Security, Identity, & Compliance', 8.400000000000003E-4, 'AWS Key Management Service', 'AWS Key Management Service', 'AWS', false, 0, false),
    ('Security, Identity, & Compliance', 1.0E-4, 'AWS Secrets Manager', 'AWS Secrets Manager', 'AWS', false, 0, false),
    ('Networking & Content Delivery', 0.0237895, 'Amazon API Gateway', 'Amazon API Gateway', 'AWS', false, 0, false),
    ('Networking & Content Delivery', 0.0117266425, 'Amazon CloudFront', 'Amazon CloudFront', 'AWS', false, 0, false),
    ('Networking & Content Delivery', 0.0, 'Amazon Virtual Private Cloud', 'Amazon Virtual Private Cloud', 'AWS', false, 0, false);

-- product_portion_widget_timeframe 데이터 삽입
INSERT INTO `cost_optimize`.`product_portion_widget_timeframe` (`time`, `isCurrent`) VALUES
                                                                                         ('202308', true),
                                                                                         ('202307', false),
                                                                                         ('202306', false),
                                                                                         ('202305', false),
                                                                                         ('202304', false),
                                                                                         ('202303', false),
                                                                                         ('202302', false),
                                                                                         ('202301', false),
                                                                                         ('202212', false),
                                                                                         ('202211', false),
                                                                                         ('202210', false),
                                                                                         ('202209', false);

-- product_portion_widget_account 데이터 삽입

INSERT INTO `cost_optimize`.`product_portion_widget_account` (`account`, `item`, `itemAlias`, `vendor`, `order`, `serviceGroup`) VALUES
                                                                                                                                     ('000885530975', '724342', 'yijeong-test1', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '325436', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '23454', 'Testaccount', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '865457', 'CUR 비용수집 테스트', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '2354364', 'Fin - PRD', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '768574', 'Shield Test1', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '346565', 'SSDonghee-3', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '22334', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '76454234', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '54233234', 'key_test2', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '43524', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '54233234', 'MCMP - PRD', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '4763542', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '324534', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '2314543', 'guide', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '4635745', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '243543', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '42356', 'Fin - MEA1', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '6432', 'Sec - PRD', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '453678', 'Chat - DEV', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '435623', 'dsfd', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '382377105752', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '410362304746', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '421486388841', 'Memory DB 실적용자', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '431689225654', 'Sec - DEV', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '448291620067', 'CADACKTEST', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '490518552534', 'SSDonghee-4', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '504963237656', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '517621134517', 'Marketplace - DEV', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '540298831859', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '557892227155', 'MCMP - NOS', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '583660770375', 'Alert - PRD', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '602331120558', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '607404727960', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '650032202665', 'Chat - PRD', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '702147360356', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '728040812910', 'Fin - STG', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '735209500943', 'Alert - DEV', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '748665785761', 'Data - DEV', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '752124472974', 'Data - PRD', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '762045528333', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '785463', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '3452', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '324', 'Marketplace - PRD', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '312', 'Memory DB 사용자', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '435434', 'SSDonghee-2', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '456342', 'MCMP - COM', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '98675', '', 'AWS', 0, false),
                                                                                                                                     ('000885530975', '4235', '', 'AWS', 0, false);









-- 2.abnormal, billing, common
-- abnormal_data 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`abnormal_data` (
    `siteId`, `companyId`, `userId`, `dashboardIndex`, `widgetIndex`,
    `level`, `detcBy`, `vendor`, `analDt`, `cost`, `rate`, `detcDt`,
    `alarmYn`, `hideYn`, `impotYn`, `creaDt`, `minAlert`, `maxAlert`,
    `dashboardTitle`, `savedHistory`, `userEmail`, `sensitivity`, `widgetTitle`
) VALUES
      ('MCMP', '1', 'MCMP', 0, 9, 'critical', 'USER', 'AWS', '2023-08-02', 0.0, 0.0, '2023-08-06 00:00', 'N', 'N', 'N', '2023-08-06', 100.0, 1000.0, 'Dashboard', TRUE, null, null, null),
      ('MCMP', '1', 'MCMP', 6, 9, 'critical', 'USER', 'AWS', '2023-08-02', 0.0, 0.0, '2023-08-06 00:00', 'N', 'N', 'N', '2023-08-06', 100.0, 1000.0, 'MCMP', TRUE, null, null, null),
      ('MCMP', '1', 'MCMP', 4, 0, 'minor', 'USER', 'AZURE', '2023-08-02', 0.0, 0.0, '2023-08-04 00:00', 'N', 'N', 'N', '2023-08-04', 100.0, 1000.0, 'abnormal-detection', true, null, null, null),
      ('MCMP', '1', 'MCMP', 0, 9, 'major', 'USER', 'AWS', '2023-08-01', 0.0, 0.0, '2023-08-05 00:00', 'N', 'N', 'N', '2023-08-05', 100.0, 1000.0, 'Dashboard', true, null, null, null),
      ('MCMP', '1', 'MCMP', 6, 9, 'major', 'USER', 'AWS', '2023-08-01', 0.0, 0.0, '2023-08-05 00:00', 'N', 'N', 'N', '2023-08-05', 100.0, 1000.0, 'MCMP', true, null, null, null),
      ('MCMP', '1', 'MCMP', 4, 0, 'minor', 'USER', 'AZURE', '2023-08-01', 0.0, 0.0, '2023-08-03 00:00', 'N', 'N', 'N', '2023-08-03', 100.0, 1000.0, 'abnormal-detection', true, null, null, null),
      ('MCMP', '1', 'MCMP', 4, 0, 'minor', 'USER', 'AZURE', '2023-07-29', 0.0, 0.0, '2023-08-02 00:00', 'N', 'N', 'N', '2023-08-02', 100.0, 1000.0, 'abnormal-detection', true, null, null, null),
      ('MCMP', '1', 'MCMP', 4, 0, 'minor', 'USER', 'AZURE', '2023-07-29', 0.0, 0.0, '2023-08-01 00:00', 'N', 'N', 'N', '2023-08-01', 100.0, 1000.0, 'abnormal-detection', true, null, null, null),
      ('MCMP', '1', 'MCMP', 0, 9, 'critical', 'USER', 'AWS', '2023-07-28', 0.0, 0.0, '2023-08-04 00:00', 'N', 'N', 'N', '2023-08-04', 100.0, 1000.0, 'Dashboard', true, null, null, null),
      ('MCMP', '1', 'MCMP', 6, 9, 'critical', 'USER', 'AWS', '2023-07-28', 0.0, 0.0, '2023-08-04 00:00', 'N', 'N', 'N', '2023-08-04', 100.0, 1000.0, 'MCMP', true, null, null, null),
      ('MCMP', '1', 'MCMP', 0, 9, 'critical', 'USER', 'AWS', '2023-07-28', 0.0, 0.0, '2023-08-03 00:00', 'N', 'N', 'N', '2023-08-03', 100.0, 1000.0, 'Dashboard', true, null, null, null),
      ('MCMP', '1', 'MCMP', 6, 9, 'critical', 'USER', 'AWS', '2023-07-28', 0.0, 0.0, '2023-08-03 00:00', 'N', 'N', 'N', '2023-08-03', 100.0, 1000.0, 'MCMP', true, null, null, null);

-- abnormal_message 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`abnormal_message` (
    `id`, `siteId`, `companyId`, `userId`, `item`, `message_cost`,
    `message_rate`, `message_vendor`, `message_count`, `currency`,
    `timeFrame`, `viewBy`
) VALUES
      (1, 'MCMP', '1', 'MCMP', '421486388841,216093335544,099888603232,338715797467,971924526134,557892227155', 13658.332639155, 319.04, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account'),
      (2, 'MCMP', '1', 'MCMP', '421486388841,216093335544,099888603232,338715797467,971924526134,557892227155', 13658.332639155, 319.04, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account');

-- abnormal_message 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`abnormal_message` (
    `id`, `siteId`, `companyId`, `userId`, `item`, `message_cost`,
    `message_rate`, `message_vendor`, `message_count`, `currency`,
    `timeFrame`, `viewBy`
) VALUES
      (3, 'MCMP', '1', 'MCMP', 'Virtual Machines|Virtual Machines Dv3 Series,Storage|Standard Page Blob,Storage|Standard HDD Managed Disks,Bandwidth|Rtn Preference: MGN,Storage|Premium SSD Managed Disks,Virtual Network|IP Addresses', -2.8342680609958233, -17.74, 'AZURE', 6, 'KRW', 'compare_this_month_vs_last_month', 'product'),
      (4, 'MCMP', '1', 'MCMP', '421486388841,216093335544,370166107047,338715797467,971924526134,557892227155', 277.2139783691, 11.2, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account'),
      (5, 'MCMP', '1', 'MCMP', '421486388841,216093335544,370166107047,338715797467,971924526134,557892227155', 277.2139783691, 11.2, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account');


-- abnormal_message 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`abnormal_message` (
    `id`, `siteId`, `companyId`, `userId`, `item`, `message_cost`,
    `message_rate`, `message_vendor`, `message_count`, `currency`,
    `timeFrame`, `viewBy`
) VALUES
      (6, 'MCMP', '1', 'MCMP', 'Virtual Machines|Virtual Machines Dv3 Series,Storage|Standard Page Blob,Storage|Standard HDD Managed Disks,Bandwidth|Rtn Preference: MGN,Storage|Premium SSD Managed Disks,Virtual Network|IP Addresses', -2.7233889261976767, -33.59, 'AZURE', 6, 'KRW', 'compare_this_month_vs_last_month', 'product'),
      (7, 'MCMP', '1', 'MCMP', 'Storage|Standard Page Blob,Storage|Standard HDD Managed Disks,Virtual Machines|Dv3/DSv3 Series,Azure Database for PostgreSQL|Flexible Server General Purpose Dv4 Series Compute,Azure Database for PostgreSQL|Flexible Server Storage,Azure Database for MariaDB|Basic - Compute Gen5,Storage|Premium SSD Managed Disks,Azure Database for MySQL|Flexible Server Storage,Azure Database for MariaDB|Basic - Storage,Storage|Files,Storage|General Block Blob,Virtual Network|IP Addresses,Azure Database for MySQL|Flexible Server Burstable BS Series Compute,Storage|Tiered Block Blob', -147.65234519891365, -71.72, 'AZURE', 14, 'KRW', 'compare_this_month_vs_last_month', 'product'),
      (8, 'MCMP', '1', 'MCMP', 'Storage|Standard Page Blob,Storage|Standard HDD Managed Disks,Virtual Machines|Dv3/DSv3 Series,Azure Database for PostgreSQL|Flexible Server General Purpose Dv4 Series Compute,Azure Database for PostgreSQL|Flexible Server Storage,Azure Database for MariaDB|Basic - Compute Gen5,Storage|Premium SSD Managed Disks,Azure Database for MySQL|Flexible Server Storage,Azure Database for MariaDB|Basic - Storage,Storage|Files,Storage|General Block Blob,Virtual Network|IP Addresses,Azure Database for MySQL|Flexible Server Burstable BS Series Compute,Storage|Tiered Block Blob', -146.93715415185648, -71.72, 'AZURE', 14, 'KRW', 'compare_this_month_vs_last_month', 'product');

-- abnormal_message 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`abnormal_message` (
    `id`, `siteId`, `companyId`, `userId`, `item`, `message_cost`,
    `message_rate`, `message_vendor`, `message_count`, `currency`,
    `timeFrame`, `viewBy`
) VALUES
      (9, 'MCMP', '1', 'MCMP', '353816306145,650032202665,099888603232,338715797467,557892227155,068088353237', 10687.616171101105, 132.79, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account'),
      (10, 'MCMP', '1', 'MCMP', '353816306145,650032202665,099888603232,338715797467,557892227155,068088353237', 10687.616171101105, 132.79, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account'),
      (11, 'MCMP', '1', 'MCMP', '353816306145,650032202665,099888603232,338715797467,557892227155,068088353237', 10687.616171101105, 132.79, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account'),
      (12, 'MCMP', '1', 'MCMP', '353816306145,650032202665,099888603232,338715797467,557892227155,068088353237', 10687.616171101105, 132.79, 'AWS', 6, 'KRW', 'compare_this_month_vs_last_month', 'account');

-- common insert
-- vendor_status
INSERT INTO `cost_optimize`.`vendor_status` (
    `siteCd`, `mspVndrId`, `mspVndrStatus`
) VALUES
      ('MCMP', 'aws', 'Y'),
      ('MCMP', 'azu', 'Y'),
      ('MCMP', 'azu-csp', 'Y'),
      ('MCMP', 'gcp', 'Y'),
      ('MCMP', 'ncp', 'Y'),
      ('MCMP', 'idc', 'N'),
      ('MCMP', 'oci', 'Y'),
      ('MCMP', 'kt', 'Y'),
      ('MCMP', 'nhn', 'Y'),
      ('MCMP', 'openstack', 'Y'),
      ('MCMP', 'tencent', 'Y'),
      ('MCMP', 'ali', 'Y'),
      ('MCMP', 'huawei', 'N');

-- companyList
INSERT INTO `cost_optimize`.`company_vendor_account_list` (accId, alias, hlthYn, vendor)
VALUES
    ('0', 'Marketplace - DEV', 'Y', 'aws'),
    ('0', 'Testaccount', 'Y', 'aws'),
    ('0', 'MCMP - NOS', 'Y', 'aws'),
    ('0', 'CUR 비용수집 테스트', 'Y', 'aws'),
    ('0', 'AutoSpot-Anhvtl', 'Y', 'aws'),
    ('0', 'Shield Test1', 'Y', 'aws'),
    ('0', 'Fin - STG', 'Y', 'aws'),
    ('0', 'MCMP - PRD', 'Y', 'aws'),
    ('0', 'Data - DEV', 'Y', 'aws'),
    ('0', 'Fin - MEA1', 'Y', 'aws'),
    ('0', 'Marketplace - PRD', 'Y', 'aws'),
    ('0', 'Chat - DEV', 'Y', 'aws'),
    ('0', 'Sec - DEV', 'Y', 'aws'),
    ('0', '계정테스트입니다--', 'Y', 'aws'),
    ('0', 'hy-test', 'Y', 'aws'),
    ('0', 'CBP test용 계정_JJ', 'Y', 'aws'),
    ('0', 'jongwon-개인계정', 'Y', 'aws'),
    ('0', 'Alert - PRD', 'Y', 'aws'),
    ('0', 'Fin - PRD', 'N', 'aws'),
    ('0', 'Chat - PRD', 'Y', 'aws'),
    ('0', 'key_test2', 'Y', 'aws'),
    ('0', 'Alert - DEV', 'Y', 'aws'),
    ('0', 'guide', 'N', 'aws'),
    ('0', 'Data - PRD', 'Y', 'aws'),
    ('0', 'Sec - PRD', 'Y', 'aws'),
    ('0', 'MCMP - COM', 'Y', 'aws'),
    ('0', 'dsfd', 'Y', 'aws'),
    ('0', 'Autospot-test', 'Y', 'aws'),
    ('0', 'yijeong-test1', 'Y', 'aws'),
    ('0', 'AP구독_1', 'Y', 'azu'),
    ('0', 'MCMP Svc 2', 'Y', 'azu'),
    ('0', '요금재계산테스트', 'Y', 'azu'),
    ('0', 'MCMP-SWQA', 'Y', 'azu'),
    ('0', 'SWCAMP01', 'Y', 'azu'),
    ('0', 'Azure subscription 1', 'Y', 'azu'),
    ('0', 'SWCAMP02', 'Y', 'azu'),
    ('0', 'Autospot-Anhvtl', 'N', 'gcp'),
    ('0', 'MCMP GCP Billing', 'Y', 'gcp'),
    ('0', 'MCMP-qa-automation', 'Y', 'gcp'),
    ('0', 'test040', 'Y', 'gcp'),
    ('0', 'iaas-demo', 'Y', 'gcp'),
    ('0', 'MCMP-gcp-dev-pjt', 'Y', 'gcp'),
    ('0', 'smart-osc', 'N', 'gcp'),
    ('0', '12', 'N', 'ncp'),
    ('0', 'MCMPpsteam_bgk', 'N', 'ncp'),
    ('0', 'MCMP 테스트', 'N', 'ncp'),
    ('0', 'test', 'N', 'ncp'),
    ('0', 'asset_svc2', 'N', 'ncp'),
    ('0', 'p5', 'N', 'ncp'),
    ('0', 'dongdongdong', 'N', 'ncp'),
    ('0', 'mcmp', 'Y', 'oci'),
    ('0', '404', 'N', 'tencent');

-- company_vendor 테이블 데이터 삽입
INSERT INTO `cost_optimize`.`company_vendor` (cloudVndrId, cloudVndrNm)
VALUES
    ('aws', 'Amzon Web Service'),
    ('azu', 'MicroSoft Azure'),
    ('gcp', 'GCP'),
    ('ncp', 'NCP'),
    ('oci', 'OCI'),
    ('tencent', 'Tencent');

-- currency
INSERT INTO `cost_optimize`.`currencies` (
    `currency_code`, `exchange_rate`, `exchangeRateDate`
) VALUES
      ('KRW', 1338.0, '2023-08-24'),
      ('MXN', 17.06, '2023-08-24'),
      ('IDR', 152.91, '2023-08-24'),
      ('USD', 1.0, '2023-08-24'),
      ('VND', 26760.0, '2023-08-24'),
      ('CNY', 7.3, '2023-08-24');

-- user_info
INSERT INTO `cost_optimize`.`billing_detail` (
    `linkedAccountId`, `linkedAccountAlias`, `productName`, `regionName`,
    `usageType`, `invoiceId`, `tagValue`, `tagKey`, `usage`, `cost`,
    `itemDescription`, `serviceGroup`, `domainName`, `invoiceType`
) VALUES
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Mumbai)', 'APS3-InsightsEvents', '1372115401', NULL, NULL, 5976.0, 0.020915999999999997, '0.0000035 per event analyzed in Asia Pacific (Mumbai) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Osaka)', 'APN3-FreeEventsRecorded', '1372115401', NULL, NULL, 364599.0, 0.0, '0.0 per free event recorded in Asia Pacific (Osaka) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Seoul)', 'APN2-FreeEventsRecorded', '1372115401', NULL, NULL, 4395224.0, 0.0, '0.0 per free event recorded in Asia Pacific (Seoul) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Seoul)', 'APN2-InsightsEvents', '1372115401', NULL, NULL, 6136.0, 0.021475999999999995, '0.0000035 per event analyzed in Asia Pacific (Seoul) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Singapore)', 'APS1-FreeEventsRecorded', '1372115401', NULL, NULL, 459523.0, 0.0, '0.0 per free event recorded in Asia Pacific (Singapore) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Singapore)', 'APS1-InsightsEvents', '1372115401', NULL, NULL, 5976.0, 0.020915999999999997, '0.0000035 per event analyzed in Asia Pacific (Singapore) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Sydney)', 'APS2-FreeEventsRecorded', '1372115401', NULL, NULL, 461381.0, 0.0, '0.0 per free event recorded in Asia Pacific (Sydney) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Sydney)', 'APS2-InsightsEvents', '1372115401', NULL, NULL, 6006.0, 0.021021, '0.0000035 per event analyzed in Asia Pacific (Sydney) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Tokyo)', 'APN1-FreeEventsRecorded', '1372115401', NULL, NULL, 521884.0, 0.0, '0.0 per free event recorded in Asia Pacific (Tokyo) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Asia Pacific (Tokyo)', 'APN1-InsightsEvents', '1372115401', NULL, NULL, 5979.0, 0.020926499999999997, '0.0000035 per event analyzed in Asia Pacific (Tokyo) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Canada (Central)', 'CAN1-FreeEventsRecorded', '1372115401', NULL, NULL, 413982.0, 0.0, '0.0 per free event recorded in Canada (Central) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Canada (Central)', 'CAN1-InsightsEvents', '1372115401', NULL, NULL, 5983.0, 0.020940499999999997, '0.0000035 per event analyzed in Canada (Central) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Frankfurt)', 'EUC1-FreeEventsRecorded', '1372115401', NULL, NULL, 481785.0, 0.0, '0.0 per free event recorded in EU (Frankfurt) region', NULL, NULL, NULL);


INSERT INTO `cost_optimize`.`billing_detail` (
    `linkedAccountId`, `linkedAccountAlias`, `productName`, `regionName`,
    `usageType`, `invoiceId`, `tagValue`, `tagKey`, `usage`, `cost`,
    `itemDescription`, `serviceGroup`, `domainName`, `invoiceType`
) VALUES
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Frankfurt)', 'EUC1-InsightsEvents', '1372115401', NULL, NULL, 5972.0, 0.020902, '0.0000035 per event analyzed in EU (Frankfurt) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Ireland)', 'EU-FreeEventsRecorded', '1372115401', NULL, NULL, 475452.0, 0.0, '0.0 per free event recorded in EU (Dublin) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Ireland)', 'EU-InsightsEvents', '1372115401', NULL, NULL, 5978.0, 0.020922999999999994, '0.0000035 per event analyzed in EU (Dublin) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (London)', 'EUW2-FreeEventsRecorded', '1372115401', NULL, NULL, 426452.0, 0.0, '0.0 per free event recorded in EU (London) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (London)', 'EUW2-InsightsEvents', '1372115401', NULL, NULL, 5972.0, 0.020901999999999997, '0.0000035 per event analyzed in EU (London) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Paris)', 'EUW3-FreeEventsRecorded', '1372115401', NULL, NULL, 400789.0, 0.0, '0.0 per free event recorded in EU (Paris) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Paris)', 'EUW3-InsightsEvents', '1372115401', NULL, NULL, 5968.0, 0.020888000000000004, '0.0000035 per event analyzed in EU (Paris) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Stockholm)', 'EUN1-FreeEventsRecorded', '1372115401', NULL, NULL, 402583.0, 0.0, '0.0 per free event recorded in EU (Stockholm) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Europe (Stockholm)', 'EUN1-InsightsEvents', '1372115401', NULL, NULL, 5977.0, 0.020919499999999994, '0.0000035 per event analyzed in EU (Stockholm) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Middle East (Bahrain)', 'MES1-FreeEventsRecorded', '1372115401', NULL, NULL, 392876.0, 0.0, '0.0 per free event recorded in Middle East (Bahrain)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'Middle East (Bahrain)', 'MES1-InsightsEvents', '1372115401', NULL, NULL, 5975.0, 0.02091249999999999, '0.0000035 per event analyzed in Middle East (Bahrain)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'South America (Sao Paulo)', 'SAE1-FreeEventsRecorded', '1372115401', NULL, NULL, 413599.0, 0.0, '0.0 per free event recorded in South America(Sao Paulo) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'South America (Sao Paulo)', 'SAE1-InsightsEvents', '1372115401', NULL, NULL, 5982.0, 0.020937, '0.0000035 per event analyzed in South America(Sao Paulo) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'US East (N. Virginia)', 'USE1-FreeEventsRecorded', '1372115401', NULL, NULL, 754377.0, 0.0, '0.0 per free event recorded in US East (N.Virginia) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'US East (N. Virginia)', 'USE1-InsightsEvents', '1372115401', NULL, NULL, 6002.0, 0.021006999999999998, '0.0000035 per event analyzed in US East (N.Virginia) region', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS CloudTrail', 'US East (Ohio)', 'USE2-FreeEventsRecorded', '1372115401', NULL, NULL, 507630.0, 0.0, '0.0 per free event recorded in US East (Ohio) region', NULL, NULL, NULL);

INSERT INTO `cost_optimize`.`billing_detail` (
    `linkedAccountId`, `linkedAccountAlias`, `productName`, `regionName`,
    `usageType`, `invoiceId`, `tagValue`, `tagKey`, `usage`, `cost`,
    `itemDescription`, `serviceGroup`, `domainName`, `invoiceType`
) VALUES
      ("129335065553", "key_test2", "AWS CloudTrail", "US East (Ohio)", "USE2-InsightsEvents", "1372115401", NULL, NULL, 6004.0, 0.02101399999999999, "0.0000035 per event analyzed in US East (Ohio) region", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS CloudTrail", "US West (N. California)", "USW1-FreeEventsRecorded", "1372115401", NULL, NULL, 443368.0, 0.0, "0.0 per free event recorded in US West (N.California) region", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS CloudTrail", "US West (N. California)", "USW1-InsightsEvents", "1372115401", NULL, NULL, 5980.0, 0.020929999999999997, "0.0000035 per event analyzed in US West (N.California) region", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS CloudTrail", "US West (Oregon)", "USW2-FreeEventsRecorded", "1372115401", NULL, NULL, 610246.0, 0.0, "0.0 per free event recorded in US West (Oregon) region", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS CloudTrail", "US West (Oregon)", "USW2-InsightsEvents", "1372115401", NULL, NULL, 5974.0, 0.020908999999999997, "0.0000035 per event analyzed in US West (Oregon) region", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Hong Kong)", "APE1-APN2-AWS-Out-Bytes", "1372115401", NULL, NULL, 1.0810399999999997E-5, 9.681E-7, "USD0.09 per GB for APN2-AWS-Out-Bytes in Asia Pacific (Hong Kong)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Hong Kong)", "APE1-APS2-AWS-In-Bytes", "1372115401", NULL, NULL, 0.0019280321, 0.0, "USD0.0 per GB for APS2-AWS-In-Bytes in Asia Pacific (Hong Kong)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Hong Kong)", "APE1-APS2-AWS-Out-Bytes", "1372115401", NULL, NULL, 8.307639999999996E-5, 7.452000000000001E-6, "USD0.09 per GB for APS2-AWS-Out-Bytes in Asia Pacific (Hong Kong)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Hong Kong)", "APE1-DataTransfer-In-Bytes", "1372115401", NULL, NULL, 1.1640629999999999E-4, 0.0, "USD0.0 per GB for DataTransfer-In-Bytes in Asia Pacific (Hong Kong)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Hong Kong)", "APE1-DataTransfer-Out-Bytes", "1372115401", NULL, NULL, 2.7236900000000003E-5, 3.2701999999999984E-6, "USD0.12 per GB for DataTransfer-Out-Bytes in Asia Pacific (Hong Kong)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Hong Kong)", "APE1-USE1-AWS-In-Bytes", "1372115401", NULL, NULL, 3.72557E-5, 0.0, "USD0.0 per GB for USE1-AWS-In-Bytes in Asia Pacific (Hong Kong)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Hong Kong)", "APE1-USE1-AWS-Out-Bytes", "1372115401", NULL, NULL, 1.1810999999999998E-6, 1.0660000000000003E-7, "USD0.09 per GB for USE1-AWS-Out-Bytes in Asia Pacific (Hong Kong)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Mumbai)", "APS3-APN2-AWS-Out-Bytes", "1372115401", NULL, NULL, 1.0810399999999996E-5, 9.289000000000003E-7, "$0.086 per GB - Asia Pacific (Mumbai) data transfer to Asia Pacific (Seoul)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Mumbai)", "APS3-APS2-AWS-In-Bytes", "1372115401", NULL, NULL, 0.0019308604999999995, 0.0, "$0.00 per GB - Asia Pacific (Mumbai) data transfer from Asia Pacific (Sydney)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Mumbai)", "APS3-APS2-AWS-Out-Bytes", "1372115401", NULL, NULL, 8.293649999999997E-5, 7.124000000000002E-6, "$0.086 per GB - Asia Pacific (Mumbai) data transfer to Asia Pacific (Sydney)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Mumbai)", "APS3-DataTransfer-In-Bytes", "1372115401", NULL, NULL, 1.1657350000000002E-4, 0.0, "$0.000 per GB - data transfer in per month", NULL, NULL, NULL);

INSERT INTO `cost_optimize`.`billing_detail` (
    `linkedAccountId`, `linkedAccountAlias`, `productName`, `regionName`,
    `usageType`, `invoiceId`, `tagValue`, `tagKey`, `usage`, `cost`,
    `itemDescription`, `serviceGroup`, `domainName`, `invoiceType`
) VALUES
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Mumbai)", "APS3-DataTransfer-Out-Bytes", "1372115401", NULL, NULL, 2.7318800000000014E-5, 2.9878E-6, "$0.1093 per GB - first 10 TB / month data transfer out beyond the global free tier", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Mumbai)", "APS3-USE1-AWS-In-Bytes", "1372115401", NULL, NULL, 3.7294499999999987E-5, 0.0, "$0.00 per GB - Asia Pacific (Mumbai) data transfer from US East (Northern Virginia)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Mumbai)", "APS3-USE1-AWS-Out-Bytes", "1372115401", NULL, NULL, 1.1810999999999998E-6, 1.0160000000000002E-7, "$0.086 per GB - Asia Pacific (Mumbai) data transfer to US East (Northern Virginia)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Osaka)", "APN3-APN2-AWS-Out-Bytes", "1372115401", NULL, NULL, 1.0810399999999996E-5, 9.681E-7, "$0.09 per GB - Asia Pacific (Osaka) data transfer to Asia Pacific (Seoul)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Osaka)", "APN3-APS2-AWS-In-Bytes", "1372115401", NULL, NULL, 0.0019421718, 0.0, "$0.00 per GB - Asia Pacific (Osaka) data transfer from Asia Pacific (Sydney)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Osaka)", "APN3-APS2-AWS-Out-Bytes", "1372115401", NULL, NULL, 8.293649999999999E-5, 7.4393E-6, "$0.09 per GB - Asia Pacific (Osaka) data transfer to Asia Pacific (Sydney)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Osaka)", "APN3-DataTransfer-In-Bytes", "1372115401", NULL, NULL, 1.172526E-4, 0.0, "$0.000 per GB - data transfer in per month", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Osaka)", "APN3-DataTransfer-Out-Bytes", "1372115401", NULL, NULL, 2.7655500000000003E-5, 3.1517999999999988E-6, "$0.114 per GB - first 10 TB / month data transfer out beyond the global free tier", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Osaka)", "APN3-USE1-AWS-In-Bytes", "1372115401", NULL, NULL, 3.7468E-5, 0.0, "$0.00 per GB - Asia Pacific (Osaka) data transfer from US East (Northern Virginia)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Osaka)", "APN3-USE1-AWS-Out-Bytes", "1372115401", NULL, NULL, 1.1810999999999998E-6, 1.0660000000000003E-7, "$0.09 per GB - Asia Pacific (Osaka) data transfer to US East (Northern Virginia)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Seoul)", "APN2-AFS1-AWS-In-Bytes", "1372115401", NULL, NULL, 2.6847392957000005, 0.0, "USD 0.0 per GB for APN2-AWS-In-Bytes in Africa (Cape Town)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Seoul)", "APN2-AFS1-AWS-Out-Bytes", "1372115401", NULL, NULL, 1.1029750996000003, 0.08823800690000004, "USD 0.08 per GB for EUN1-AWS-Out-Bytes in Africa (Cape Town)", NULL, NULL, NULL),
      ("129335065553", "key_test2", "AWS Data Transfer", "Asia Pacific (Seoul)", "APN2-APE1-AWS-In-Bytes", "1372115401", NULL, NULL, 2.508768350500001, 0.0, "USD0.0 per GB for  in Asia Pacific (Hong Kong)", NULL, NULL, NULL);

INSERT INTO `cost_optimize`.`billing_detail` (
    `linkedAccountId`, `linkedAccountAlias`, `productName`, `regionName`,
    `usageType`, `invoiceId`, `tagValue`, `tagKey`, `usage`, `cost`,
    `itemDescription`, `serviceGroup`, `domainName`, `invoiceType`
) VALUES
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APN1-AWS-In-Bytes', '1372115401', NULL, NULL, 24.463793425099997, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Tokyo)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APN1-AWS-Out-Bytes', '1372115401', NULL, NULL, 2.2915414070999995, 0.18332331390000006, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Tokyo)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APN3-AWS-In-Bytes', '1372115401', NULL, NULL, 2.9914386521000007, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Osaka)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APN3-AWS-Out-Bytes', '1372115401', NULL, NULL, 1.3410469481, 0.10728375639999999, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Osaka)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS1-AWS-In-Bytes', '1372115401', NULL, NULL, 4.6766552457, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Singapore)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS1-AWS-Out-Bytes', '1372115401', NULL, NULL, 2.020732887, 0.1616586299, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Singapore)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS2-AWS-In-Bytes', '1372115401', NULL, NULL, 4.5988399542, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Sydney)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS2-AWS-Out-Bytes', '1372115401', NULL, NULL, 2.010030785000001, 0.16080243840000003, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Sydney)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS3-AWS-In-Bytes', '1372115401', NULL, NULL, 4.0428497175, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Mumbai)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS3-AWS-Out-Bytes', '1372115401', NULL, NULL, 1.6927172302000002, 0.13541737819999997, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Mumbai)', NULL, NULL, NULL);


INSERT INTO `cost_optimize`.`billing_detail` (
    `linkedAccountId`, `linkedAccountAlias`, `productName`, `regionName`,
    `usageType`, `invoiceId`, `tagValue`, `tagKey`, `usage`, `cost`,
    `itemDescription`, `serviceGroup`, `domainName`, `invoiceType`
) VALUES
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS4-AWS-In-Bytes', '1372115401', NULL, NULL, 2.5037750425999996, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Jakarta)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS4-AWS-Out-Bytes', '1372115401', NULL, NULL, 1.0492293606, 0.0839383529, '$0.08 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Jakarta)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS5-AWS-In-Bytes', '1372115401', NULL, NULL, 2.4094801707, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Hyderabad)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS5-AWS-Out-Bytes', '1372115401', NULL, NULL, 1.0151751083, 0.0812140113, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Hyderabad)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS6-AWS-In-Bytes', '1372115401', NULL, NULL, 2.2991944067, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Asia Pacific (Melbourne)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-APS6-AWS-Out-Bytes', '1372115401', NULL, NULL, 0.9637462232, 0.07709969969999998, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Asia Pacific (Melbourne)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-BOS1-AWS-In-Bytes', '1372115401', NULL, NULL, 4.47E-8, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from US East (Boston)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-BOS1-AWS-Out-Bytes', '1372115401', NULL, NULL, 4.47E-8, 3.6E-9, '$0.080 per GB - Asia Pacific (Seoul) data transfer to US East (Boston)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-CAN1-AWS-In-Bytes', '1372115401', NULL, NULL, 3.140240722399999, 0.0, '$0.00 per GB - Asia Pacific (Seoul) data transfer from Canada (Central)', NULL, NULL, NULL);


INSERT INTO `cost_optimize`.`billing_detail` (
    `linkedAccountId`, `linkedAccountAlias`, `productName`, `regionName`,
    `usageType`, `invoiceId`, `tagValue`, `tagKey`, `usage`, `cost`,
    `itemDescription`, `serviceGroup`, `domainName`, `invoiceType`
) VALUES
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-CAN1-AWS-Out-Bytes', '1372115401', NULL, NULL, 1.4240357750000001, 0.11392286219999995, '$0.080 per GB - Asia Pacific (Seoul) data transfer to Canada (Central)', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-CloudFront-In-Bytes', '1372115401', NULL, NULL, 114.1630315607, 0.0, '$0.00 per GB data transfer in to Asia Pacific (Seoul) from CloudFront', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-CloudFront-Out-Bytes', '1372115401', NULL, NULL, 0.7930481553000002, 0.0, '$0.00 per GB data transfer out of Asia Pacific (Seoul) to CloudFront', NULL, NULL, NULL),
      ('129335065553', 'key_test2', 'AWS Data Transfer', 'Asia Pacific (Seoul)', 'APN2-DataTransfer-In-Bytes', '1372115401', NULL, NULL, 760.5779172888998, 0.0, '$0.000 per GB - data transfer in per month', NULL, NULL, NULL);

-- charge
-- billing_charge 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`billing_charge` (
    `linkedAccountId`,
    `linkedAccountAlias`,
    `vendor`,
    `chargeYear`,
    `chargeMonth`,
    `totalCharge`,
    `invoiceCurrency`,
    `companyCurrency`,
    `cloudCost`,
    `cloudOriginalCost`,
    `onDemandDiscount`,
    `cloudFrontDiscount`,
    `cloudFrontDtoDiscount`,
    `cloudFrontReqDiscount`,
    `cloudServiceCharge`,
    `exchangedCloudServiceCharge`,
    `salesDiscount`,
    `salesDiscountApplyType`,
    `salesDiscountApplyValue`,
    `supportFee`,
    `supportFeeApplyValue`,
    `credit`
) VALUES
      ('000885530975', 'yijeong-test1', 'AWS', '2023', '08', 15.0835920025, 'USD', 'KRW', 15.0835920025, 15.0835920025, 0.0, 0.0, 0.0, 0.0, 15.0835920025, 15.0835920025, 0.0, '', 0.0, 0.0, 0.0, 0.0),
      ('068088353237', 'CUR 비용수집 테스트', 'AWS', '2023', '08', 5.2905599675, 'USD', 'KRW', 5.2905599675, 5.2905599675, 0.0, 0.0, 0.0, 0.0, 5.2905599675, 5.2905599675, 0.0, '', 0.0, 0.0, 0.0, 0.0 ),
      ('099888603232', 'Shield Test1', 'AWS', '2023', '08', 267.3695035662, 'USD', 'KRW', 267.3695035662, 273.27952272870004, 5.9004826625, 0.0095365, 0.0, 0.0095365, 267.3695035662, 267.3695035662, 0.0, 'I', 10.0, 0.0, 114.0, 0.0),
      ('129335065553', 'key_test2', 'AWS', '2023', '08', 583.0043936331, 'USD', 'KRW', 583.0043936331, 626.8893936331, 43.885, 0.0, 0.0, 0.0, 583.0043936331, 583.0043936331, 0.0, 'S', 123.0, 0.0, 114.0, 0.0),
      ('216093335544', 'MCMP - PRD', 'AWS', '2023', '08', 48093.3628688988, 'USD', 'KRW', 45379.4233084211, 46239.922055749405, 860.4987473283, 0.0, 0.0, 0.0, 48093.3628688988, 48093.3628688988, 0.0, 'R', 1500.0, 2713.9395604777, 121.0, 0.0),
      ('338715797467', 'Fin - MEA1', 'AWS', '2023', '08', 14798.40161082599, 'USD', 'KRW', 4608.3497813649, 4862.4613789842, 254.1115976193, 0.0, 0.0, 0.0, 4337.56663268599, 4337.56663268599, 270.78314867891, 'R', 10.0, 0.0, 10.0, 0.0);


-- billing_charge_additional_services 테이블에 데이터 삽입
INSERT INTO `cost_optimize`.`billing_charge_additional_services` (
    `linkedAccountId`,
    `linkedAccountAlias`,
    `vendor`,
    `chargeYear`,
    `chargeMonth`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      ('338715797467', 'Fin - MEA1', 'AWS', '2023', '08', 'case2', 'MCMP101', 10000.0),
      ('338715797467', 'Fin - MEA1', 'AWS', '2023', '08', 'case3', 'MCMP102', 460.8349781399999);

-- billing_charge
INSERT INTO `cost_optimize`.`billing_charge` (
    `linkedAccountId`,
    `linkedAccountAlias`,
    `vendor`,
    `chargeYear`,
    `chargeMonth`,
    `totalCharge`,
    `invoiceCurrency`,
    `companyCurrency`,
    `cloudCost`,
    `cloudOriginalCost`,
    `onDemandDiscount`,
    `cloudFrontDiscount`,
    `cloudFrontDtoDiscount`,
    `cloudFrontReqDiscount`,
    `cloudServiceCharge`,
    `exchangedCloudServiceCharge`,
    `salesDiscount`,
    `salesDiscountApplyType`,
    `salesDiscountApplyValue`,
    `supportFee`,
    `supportFeeApplyValue`,
    `credit`
) VALUES
      ('353816306145', 'Chat - DEV', 'AWS', '2023', '08', 20368.16899656992, 'USD', 'KRW', 349.7954938736, 351.3550138736, 1.55952, 0.0, 0.0, 0.0, 333.18944717991997, 333.18944717991997, 34.095821387360004, 'R', 10.0, 17.48977469368, 10.0, 0.0),
      ('370166107047', 'dsfd', 'AWS', '2023', '08', 38640.70703062751, 'USD', 'KRW', 7093.7296078269, 7257.3218373265, 163.5922294996, 0.0, 0.0, 0.0, 7200.58919218751, 7200.589192187509, 602.51337642208, 'R', 10.0, 709.37296078269, 10.0, 0.0),
      ('421486388841', 'Memory DB 실적용자', 'AWS', '2023', '08', 2356.0550981699, 'USD', 'KRW', 2356.0550981699, 2359.2840581698997, 3.22896, 0.0, 0.0, 0.0, 2356.0550981699, 2356.0550981699, 0.0, '', 0.0, 0.0, 0.0, 0.0),
      ('517621134517', 'Marketplace - DEV', 'AWS', '2023', '08', 1400.9177220371, 'USD', 'KRW', 1400.9177220371, 1412.0500362339, 11.1323141968, 0.0, 0.0, 0.0, 1400.9177220371, 1400.9177220371, 0.0, 'R', 10.0, 0.0, 121.0, 0.0),
      ('557892227155', 'MCMP - NOS', 'AWS', '2023', '08', 8109.4457729001, 'USD', 'KRW', 8109.4457729001, 8109.4457729001, 0.0, 0.0, 0.0, 0.0, 8109.4457729001, 8109.4457729001, 0.0, '', 0.0, 0.0, 0.0, 0.0);

-- billing_charge_additional_services
INSERT INTO `cost_optimize`.`billing_charge_additional_services` (
    `linkedAccountId`,
    `linkedAccountAlias`,
    `vendor`,
    `chargeYear`,
    `chargeMonth`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      ('353816306145', 'Chat - DEV', 'AWS', '2023', '08', 'case1', 'MCMP100', 20000.0),
      ('353816306145', 'Chat - DEV', 'AWS', '2023', '08', 'test', 'MCMP098', 34.97954939),
      ('370166107047', 'dsfd', 'AWS', '2023', '08', 'GCP 베스핀 할인', 'MCMP080', 30000.0),
      ('370166107047', 'dsfd', 'AWS', '2023', '08', 'test11', 'MCMP074', 1440.11783844);


-- billing_charge
INSERT INTO `cost_optimize`.`billing_charge` (
    `linkedAccountId`,
    `linkedAccountAlias`,
    `vendor`,
    `chargeYear`,
    `chargeMonth`,
    `totalCharge`,
    `invoiceCurrency`,
    `companyCurrency`,
    `cloudCost`,
    `cloudOriginalCost`,
    `onDemandDiscount`,
    `cloudFrontDiscount`,
    `cloudFrontDtoDiscount`,
    `cloudFrontReqDiscount`,
    `cloudServiceCharge`,
    `exchangedCloudServiceCharge`,
    `salesDiscount`,
    `salesDiscountApplyType`,
    `salesDiscountApplyValue`,
    `supportFee`,
    `supportFeeApplyValue`,
    `credit`
) VALUES
      ('650032202665', 'Chat - PRD', 'AWS', '2023', '08', 279.4171120795, 'USD', 'KRW', 279.4171120795, 279.4171120795, 0.0, 0.0, 0.0, 0.0, 279.4171120795, 279.4171120795, 0.0, 'S', 0.0, 0.0, 114.0, 0.0),
      ('748665785761', 'Data - DEV', 'AWS', '2023', '08', 20.7317870229, 'USD', 'KRW', 20.7317870229, 20.7317870229, 0.0, 0.0, 0.0, 0.0, 20.7317870229, 20.7317870229, 0.0, 'I', 0.0, 0.0, 114.0, 0.0),
      ('831192334948', 'Marketplace - PRD', 'AWS', '2023', '08', 10601.720111545364, 'USD', 'KRW', 473.5164440813, 494.7222440813, 21.2058, 0.0, 0.0, 0.0, 501.433426285365, 501.433426285365, 19.434662204065003, 'R', 5.0, 47.35164440813, 10.0, 0.0),
      ('875531359846', 'Memory DB 사용자', 'AWS', '2023', '08', 186338.4115234809, 'USD', 'KRW', 186338.4115234809, 186338.4115234809, 0.0, 0.0, 0.0, 0.0, 186338.4115234809, 186338.4115234809, 0.0, '', 0.0, 0.0, 0.0, 0.0),
      ('971924526134', 'MCMP - COM', 'AWS', '2023', '08', 424.1237859788, 'USD', 'KRW', 424.1237859788, 425.78343227880003, 1.6596463, 0.0, 0.0, 0.0, 424.1237859788, 424.1237859788, 0.0, 'R', 10.0, 0.0, 10.0, 0.0);

-- billing_charge_additional_services
INSERT INTO `cost_optimize`.`billing_charge_additional_services` (
    `linkedAccountId`,
    `linkedAccountAlias`,
    `vendor`,
    `chargeYear`,
    `chargeMonth`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      ('831192334948', 'Marketplace - PRD', 'AWS', '2023', '08', '111긴서비스명MSPMCMP', 'MCMP063', 10000.0),
      ('831192334948', 'Marketplace - PRD', 'AWS', '2023', '08', '부가서비스 테스트', 'MCMP081', 100.28668525999998);

-- billing_info
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','05','2021-05-01 00:00:00','2021-05-31 00:00:00',179.67062365103084,55008590.642718524,'USD','KRW',1116.0,'2021-05-31',53881.78755539933,56448.80285442226,53881.78755539933,0.0,0.0,4189.103920228956,1622.088621206026,0.0,56448.80285442226,63003785,-7995194.35728148,'N','Y','2021-05-31',1);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','06','2021-06-01 00:00:00','2021-06-30 00:00:00',18.60846727317211,65244846.229902014,'USD','KRW',1130.0,'2021-06-30',54515.883452678114,53034.60702244866,54515.883452678114,0.0,0.0,0.0,1481.276430229449,0.0,53034.60702244866,59929107,5315739.22990201,'N','Y','2021-06-30',2);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','07','2021-07-01 00:00:00','2021-07-31 00:00:00',-4.0693439916258,62589809,'USD','KRW',1147.4,'2021-07-30',56151.501991762365,54549.24880814684,56151.501991762365,0.0,0.0,0.0,1602.2531836155276,0.0,54549.24880814684,62589809,0.0,'N','Y','2021-07-30',NULL);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','08','2021-08-01 00:00:00','2021-08-31 00:00:00',6.378980961581135,66582401,'USD','KRW',1170.8,'2021-08-30',58199.159991270426,56869.14747423686,58199.159991270426,0.0,0.0,129.0,1459.0125170335673,0.0,56869.14747423686,66582401,0.0,'N','Y','2021-08-30',NULL);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','09','2021-09-01 00:00:00','2021-09-30 00:00:00',-13.962387748678523,57285908,'USD','KRW',1181.7,'2021-09-24',49654.79340697115,48477.538477032256,49654.79340697115,0.0,0.0,0.0,1177.254929938893,0.0,48477.538477032256,57285908,0.0,'N','Y','2021-09-24',NULL);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','10','2021-10-01 00:00:00','2021-10-31 00:00:00',32.192148500272225,75727472.57308933,'USD','KRW',1171.7,'2021-10-29',58068.73449338622,60895.85157687601,58068.73449338622,0.0,0.0,3769.0855791499657,931.9684956601716,10.0,60895.85157687601,71311235,4416237.57308933,'N','Y','2021-10-29',4);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','11','2021-11-01 00:00:00','2021-11-30 00:00:00',-31.36780732953534,51973424.88083596,'USD','KRW',1193.4,'2021-11-30',52780.18158633678,39737.508869355646,52780.18158633678,0.0,0.0,3898.1674135232097,330.8401305043401,16610.0,39737.508869355646,47422743,4550681.880835961,'N','Y','2021-11-30',5);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2021','12','2021-12-01 00:00:00','2021-12-31 00:00:00',79.66675662510517,93378966.7903834,'USD','KRW',1185.5,'2021-12-31',58905.804857292824,61064.20216757162,58905.804857292824,0.0,0.0,4403.138192037029,2244.740881758242,0.0,61064.20216757162,72391612,20987354.7903834,'N','Y','2021-12-31',6);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','01','2022-01-01 00:00:00','2022-01-31 00:00:00',6.413948896073478,99368246,'USD','KRW',1202.4,'2022-01-28',65281.174226828094,68962.10258095202,67273.75562190058,1992.5813950724958,0.20491675,4845.998362646604,2157.651403595172,1000.0,68962.10258095202,82920034,16448212,'N','Y','2022-01-28',7);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','02','2022-02-01 00:00:00','2022-02-28 00:00:00',-12.345893677141078,87100348,'USD','KRW',1202.7,'2022-02-28',58591.096724303745,60168.84025908455,58591.096724303745,0.0,0.0,4027.73785366589,1949.9943188850834,500.0,60168.84025908455,72365065,14735283,'N','Y','2022-02-28',8);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','03','2022-03-01 00:00:00','2022-03-31 00:00:00',13.817416665200938,99135366,'USD','KRW',1210.8,'2022-03-31',66251.01439970093,68519.16137526381,66251.01439970093,0.0,0.0,4383.974111216065,2115.827135653204,0.0,68519.16137526381,82963001,16172365,'N','Y','2022-03-31',9);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','04','2022-04-01 00:00:00','2022-04-30 00:00:00',-22.086245185194556,77240086,'USD','KRW',1269.4,'2022-04-29',57996.16638464076,60850.911927422836,58017.68223272996,21.515848089199995,0.0036934,4119.196593669745,1285.96689897687,0.0,60850.911927422836,77244148,-4062.0,'N','Y','2022-04-29',10);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','05','2022-05-01 00:00:00','2022-05-31 00:00:00',42.90315782403454,110378522,'USD','KRW',1245.8,'2022-05-31',67474.2886657735,71937.14595442417,68226.41006518558,752.12139941209,0.07661215,5427.224881902108,1416.488992663527,300.0,71937.14595442417,89619296,20759226,'N','Y','2022-05-31',11);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','06','2022-06-01 00:00:00','2022-06-30 00:00:00',-18.470629639342334,89990914,'USD','KRW',1284.9,'2022-06-29',65391.975800528366,70017.52515835469,66249.87914646805,857.9033459396801,0.00301915,5182.603941305202,1414.9579294185633,0.0,70017.52515835469,89965519,25395.0,'N','Y','2022-06-29',12);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','07','2022-07-01 00:00:00','2022-07-31 00:00:00',-12.263652528298579,78954741,'USD','KRW',1304.0,'2022-07-29',55250.7839760257,59087.78868318493,55348.75545861551,97.9714825898,0.12110605,5397.281200219657,1458.2479756502364,200.0,59087.78868318493,77050478,1904263.0,'N','Y','2022-07-29',13);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','08','2022-08-01 00:00:00','2022-08-31 00:00:00',12.566568991721482,88876643,'USD','KRW',1347.5,'2022-08-31',60410.34808335911,64521.2218376313,60438.8636170732,28.515533714100005,1.4910866,5897.881529758938,1815.523309200842,0.0,64521.2218376313,86942346,1934297.0,'N','Y','2022-08-31',14);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','09','2022-09-01 00:00:00','2022-09-30 00:00:00',-5.8139572170834555,83709393,'USD','KRW',1434.8,'2022-09-30',53875.3367802453,56932.16512722788,53880.01688568489,4.6801054396,0.00219515,4260.419563694488,1208.2713221515041,0.0,56932.16512722788,81686270,2023123.0,'N','Y','2022-09-30',15);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','10','2022-10-01 00:00:00','2022-10-31 00:00:00',3.406099241455493,86560618,'USD','KRW',1419.3,'2022-10-31',54401.6217659269,59526.08790476738,55341.3456126592,939.7238467323001,0.00151285,5331.769001025959,1147.02670891777,0.0,59526.08790476738,84485375,2075243.0,'N','Y','2022-10-31',16);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','11','2022-11-01 00:00:00','2022-11-30 00:00:00',-0.2990701845497483,86301741,'USD','KRW',1331.5,'2022-11-30',60136.5072991701,64876.08494524506,61184.261736798595,1047.7544376285,0.0048171,5384.743214479508,1562.92000603304,130.0,64876.08494524506,84224776,2076965.0,'N','Y','2022-11-30',17);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2022','12','2022-12-01 00:00:00','2022-12-31 00:00:00',1.1543463532213138,87297962,'USD','KRW',1267.3,'2022-12-30',62005.821917509806,67292.49373652147,63247.8582014977,1242.0362839878999,0.0130992,5576.573190150117,1531.9026307838521,0.0350243425,67292.49373652147,85279776,2018186.0,'N','Y','2022-12-30',18);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','01','2023-01-01 00:00:00','2023-01-31 00:00:00',11.88213534698555,97670824,'USD','KRW',1228.7,'2023-01-31',77779.17919725132,79489.21390593401,79489.30125263441,1710.1220553831001,3.9551589,0.0,0.0,0.0873467004,79489.21390593401,97670824,0.0,'N','Y','2023-01-31',NULL);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','02','2023-02-01 00:00:00','2023-02-28 00:00:00',-2.956733527711407,94782958,'USD','KRW',1227.2,'2023-02-06',66723.8715001115,76450.14811185459,68474.59184978862,1750.7203496771,0.6014634,9548.06014048658,1572.474186853935,0.029691566665000002,76450.14811185459,94569969,212989.0,'N','Y','2023-02-28',19);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','03','2023-03-01 00:00:00','2023-03-31 00:00:00',-6.064047927265577,89035274,'USD','KRW',1303.8,'2023-03-31',68021.22313556226,68297.84141970628,69795.87756909558,1774.6544335332965,0.01183025,0.0,0.0,1498.0361493893,68297.84141970628,89035274,0.0,'N','Y','2023-03-31',NULL);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','04','2023-04-01 00:00:00','2023-04-30 00:00:00',3.601951064922872,92242281,'USD','KRW',1339.9,'2023-04-28',65298.74488400921,66315.20186115481,66450.51263162961,1151.7677476204,0.0079946,1086.4978011079893,1221.8085715827724,0.0,66315.20186115481,88855738,3386543.0,'N','Y','2023-04-28',20);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','05','2023-05-01 00:00:00','2023-05-31 00:00:00',-99.63755920745525,334323.65431775,'USD','KRW',1.0,'2023-05-31',257565.69252914537,261740.92138173955,258896.09501977707,1330.4024906316997,0.0104459,4126.047671740391,1281.221309777925,0.0,261740.92138173955,261742.0,72581.65431775,'N','Y','2023-05-31',21);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','06','2023-06-01 00:00:00','2023-06-30 00:00:00',-0.8808875756846675,331378.63878429,'USD','KRW',1.0,'2023-06-30',254200.59831875618,258450.1593465443,256162.28609783808,1961.6877790818987,1.0005598,4028.917728507648,1541.044479801403,200.0,258450.1593465443,258450.0,72928.63878429,'N','Y','2023-06-30',22);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','07','2023-07-01 00:00:00','2023-07-31 00:00:00',6.841428579715327,354049.67168515,'USD','KRW',1.0,'2023-07-31',276517.0506053588,281320.8260895801,278496.62947239756,1979.5788670388,1.16118835,4135.67262145937,1311.47600427687,0.0,281320.8260895801,281319.0,72730.67168515001,'N','Y','2023-07-31',23);
INSERT INTO cost_optimize.billing_info (chargeYear,chargeMonth,startDate,endDate,increaseDecreaseRate,totalCharge,invoiceCurrency,companyCurrency,applyExchangeRate,applyExchangeRateDate,cloudCost,cloudOriginalCost,cloudUseOriginalCost,onDemandDiscount,cloudFrontDiscount,supportFee,salesDiscount,credit,cloudServiceCharge,exchangedCloudServiceCharge,additionalServiceCharge,vatYn,billConfirmationYn,lastBillUpdateDate,billingInfoId) VALUES ('2023','08','2023-08-01 00:00:00','2023-08-23 07:00:00',-6.143051208154034,332300.21905123,'USD','KRW',1.0,'2023-08-23',256337.8911887998,260265.99241807606,257704.66548640627,1366.7742976065,0.0095365,3488.1539403622,926.827008692415,0.0,260265.99241807606,260264.0,72036.21905123,'N','N','2023-08-23',24);


-- billing_bills_additional_services
INSERT INTO `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      (1, '111긴서비스명MSPMCMP', 'MCMP063', -50000.0),
      (1, 'test++', 'MCMP092', 100000.0),
      (1, '부가서비스 테스트', 'MCMP081', -8058218.76235413),
      (2, 'test++', 'MCMP092', 5315739.2299020095),
      (3, '부가서비스 테스트', 'MCMP081', 0.0);

-- billing_bills_additional_services
INSERT INTO `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      (4, '부가서비스 테스트', 'MCMP081', 4416237.57308933),
      (5, '부가서비스 테스트', 'MCMP081', 4550681.880835961);


-- billing_bills_additional_services
INSERT INTO `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      (6, 'case1', 'MCMP100', 5118752.297387106),
      (6, 'case2', 'MCMP101', 511875.22973871056),
      (6, 'case3', 'MCMP102', 5118752.297387106),
      (6, '부가서비스 테스트', 'MCMP081', 5119222.66848337),
      (6, '스페셜 플랜 (USD)', 'MCMP129', 5118752.297387106),
      (7, 'case1', 'MCMP100', 5305875.0),
      (7, 'case2', 'MCMP101', 550997.0),
      (7, 'case3', 'MCMP102', 5285465.0),
      (7, '스페셜 플랜 (USD)', 'MCMP129', 5305875.0),
      (8, 'case1', 'MCMP100', 4753317.0),
      (8, 'case2', 'MCMP101', 475332.0),
      (8, 'case3', 'MCMP102', 4753317.0),
      (8, '스페셜 플랜 (USD)', 'MCMP129', 4753317.0),
      (9, 'case1', 'MCMP100', 5216892.0),
      (9, 'case2', 'MCMP101', 521689.0),
      (9, 'case3', 'MCMP102', 5216892.0),
      (9, '스페셜 플랜 (USD)', 'MCMP129', 5216892.0);

-- billing_bills_additional_services
INSERT INTO `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      (10, 'Promotion Compensation', 'MCMP130', -4062.0),
      (11, '111긴서비스명MSPMCMP', 'MCMP063', 465938.0),
      (11, 'case1', 'MCMP100', 6861995.0),
      (11, 'case2', 'MCMP101', 665531.0),
      (11, 'case3', 'MCMP102', 6332663.0),
      (11, 'Promotion Compensation', 'MCMP130', 71104.0),
      (11, '스페셜 플랜 (USD)', 'MCMP129', 6361995.0),
      (12, '111긴서비스명MSPMCMP', 'MCMP063', 25395.0),
      (13, '111긴서비스명MSPMCMP', 'MCMP063', 200373.0),
      (13, '222', 'MCMP064', 101458.0),
      (13, '333', 'MCMP065', 123.0),
      (13, 'case1', 'MCMP', 304125.0),
      (13, 'case2', 'MCMP101', 297184.0),
      (13, 'case3', 'MCMP102', 500000.0),
      (13, 'test11', 'MCMP074', 500000.0),
      (13, 'ㅁㄴㅇㄹ', 'MCMP099', 1000.0);


-- billing_bills_additional_services
INSERT INTO `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      (14, '111긴서비스명MSPMCMP', 'MCMP063', 202525.0),
      (14, '222', 'MCMP064', 98240.0),
      (14, '333', 'MCMP065', 123.0),
      (14, 'case1', 'MCMP100', 320081.0),
      (14, 'case2', 'MCMP101', 312328.0),
      (14, 'case3', 'MCMP102', 500000.0),
      (14, 'test11', 'MCMP074', 500000.0),
      (14, 'ㅁㄴㅇㄹ', 'MCMP099', 1000.0),
      (15, '111긴서비스명MSPMCMP', 'MCMP063', 213669.0),
      (15, '222', 'MCMP064', 96357.0),
      (15, '333', 'MCMP065', 123.0),
      (15, 'case1', 'MCMP100', 361130.0),
      (15, 'case2', 'MCMP101', 350844.0),
      (15, 'case3', 'MCMP102', 500000.0),
      (15, 'test11', 'MCMP074', 500000.0),
      (15, 'ㅁㄴㅇㄹ', 'MCMP099', 1000.0),
      (16, '111긴서비스명MSPMCMP', 'MCMP063', 270040.0),
      (16, '222', 'MCMP064', 88058.0),
      (16, '333', 'MCMP065', 123.0),
      (16, 'case1', 'MCMP100', 363408.0),
      (16, 'case2', 'MCMP101', 352614.0),
      (16, 'case3', 'MCMP102', 500000.0),
      (16, 'test11', 'MCMP074', 500000.0),
      (16, 'ㅁㄴㅇㄹ', 'MCMP099', 1000.0),
      (17, '111긴서비스명MSPMCMP', 'MCMP063', 264823.0),
      (17, '222', 'MCMP064', 79925.0),
      (17, '333', 'MCMP065', 123.0),
      (17, 'case1', 'MCMP100', 372172.0),
      (17, 'case2', 'MCMP101', 358922.0),
      (17, 'case3', 'MCMP102', 500000.0),
      (17, 'test11', 'MCMP074', 500000.0),
      (17, 'ㅁㄴㅇㄹ', 'MCMP099', 1000.0);

-- billing_bills_additional_services
INSERT INTO `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      (18, '111긴서비스명MSPMCMP', 'MCMP063', 251707.0),
      (18, '222', 'MCMP064', 72240.0),
      (18, '333', 'MCMP065', 123.0),
      (18, 'case1', 'MCMP100', 352792.0),
      (18, 'case2', 'MCMP101', 340324.0),
      (18, 'case3', 'MCMP102', 500000.0),
      (18, 'test11', 'MCMP074', 500000.0),
      (18, 'ㅁㄴㅇㄹ', 'MCMP099', 1000.0),
      (19, '111긴서비스명MSPMCMP', 'MCMP063', 194440.0),
      (19, 'case1', 'MCMP100', 20230.0),
      (19, 'USD TEST', 'MCMP123', 8319.0),
      (19, '부가서비스 테스트', 'MCMP081', -10000.0);


-- billing_bills_additional_services
INSERT INTO `cost_optimize`.`billing_bills_additional_services` (
    `billingInfoId`,
    `additionalServiceName`,
    `additionalServiceCode`,
    `additionalServiceCharge`
) VALUES
      (20, '111긴서비스명MSPMCMP', 'MCMP063', 10000.0),
      (20, 'case1', 'MCMP100', 20000.0),
      (20, 'case2', 'MCMP101', 10000.0),
      (20, 'case3', 'MCMP102', 406134.0),
      (20, 'GCP 베스핀 할인', 'MCMP080', 30000.0),
      (20, 'test', 'MCMP098', 53311.0),
      (20, 'test11', 'MCMP074', 2677771.0),
      (20, '부가서비스 테스트', 'MCMP081', 179327.0),
      (21, '111긴서비스명MSPMCMP', 'MCMP063', 10000.0),
      (21, 'case1', 'MCMP100', 20000.0),
      (21, 'case2', 'MCMP101', 10000.0),
      (21, 'case3', 'MCMP102', 320.76785393),
      (21, 'GCP 베스핀 할인', 'MCMP080', 30000.0),
      (21, 'test', 'MCMP098', 40.66746782),
      (21, 'test11', 'MCMP074', 2082.73695641),
      (21, '부가서비스 테스트', 'MCMP081', 137.48203959),
      (22, '111긴서비스명MSPMCMP', 'MCMP063', 10000.0),
      (22, 'case1', 'MCMP100', 20000.0),
      (22, 'case2', 'MCMP101', 10000.0),
      (22, 'case3', 'MCMP102', 642.03253335),
      (22, 'GCP 베스핀 할인', 'MCMP080', 30000.0),
      (22, 'test', 'MCMP098', 42.70228525),
      (22, 'test11', 'MCMP074', 2106.23439884),
      (22, '부가서비스 테스트', 'MCMP081', 137.66956685),
      (23, '111긴서비스명MSPMCMP', 'MCMP063', 10000.0),
      (23, 'case1', 'MCMP100', 20000.0),
      (23, 'case2', 'MCMP101', 10000.0),
      (23, 'case3', 'MCMP102', 523.06737245),
      (23, 'GCP 베스핀 할인', 'MCMP080', 30000.0),
      (23, 'test', 'MCMP098', 48.75768971),
      (23, 'test11', 'MCMP074', 2019.7686507000003),
      (23, '부가서비스 테스트', 'MCMP081', 139.07797229),
      (24, '111긴서비스명MSPMCMP', 'MCMP063', 10000.0),
      (24, 'case1', 'MCMP100', 20000.0),
      (24, 'case2', 'MCMP101', 10000.0),
      (24, 'case3', 'MCMP102', 460.8349781399999),
      (24, 'GCP 베스핀 할인', 'MCMP080', 30000.0),
      (24, 'test', 'MCMP098', 34.97954939),
      (24, 'test11', 'MCMP074', 1440.11783844),
      (24, '부가서비스 테스트', 'MCMP081', 100.28668525999998);





