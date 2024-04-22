package com.mcmp.slack_demo.common.model;

import org.springframework.http.HttpStatus;

public enum ResponseSpecification implements ResponseSpecTranslator{
    OK(200) {

        @Override
        public String getMessage() {
            return HttpStatus.OK.getReasonPhrase();
        }
    },

    /* 300 */
    /* 계정 공통 */
    INVALID_CLOUD_ACCOUNT(300) {

        @Override
        public String getMessage() {
            return "Invalid Cloud Account";
        }
    },
    DUPLICATE_ACCOUNT(301) {

        @Override
        public String getMessage() {
            return "Already exist Account";
        }
    },
    ANOTHER_COMPANY_USING(302) {

        @Override
        public String getMessage() {
            return "Another Company Using";
        }
    },
    NOT_MATCH_REGION(303) {

        @Override
        public String getMessage() {
            return "Not Match Region";
        }

    },

    /* AWS 계정 관련 */
    INVALID_AWS_ACCOUNT_ALIAS(305) {

        @Override
        public String getMessage() {
            return "Invalid AWS Account Alias";
        }
    },
    AWS_CROSS_ACCOUNT_ACCESS_DENIED(306) {

        @Override
        public String getMessage() {
            return "AWS Cross Account Access Denied";
        }
    },
    INVALID_AWS_ACCOUNT_KEY(307) {

        @Override
        public String getMessage() {
            return "Invalid AWS Account Key";
        }
    },
    INVALID_AWS_ACCOUNT_S3_BUCKET_NAME(308) {

        @Override
        public String getMessage() {
            return "Invalid AWS Account S3 Bucket Name";
        }
    },
    NOT_MATCH_AWS_ACCOUNT_ID_KEY(309) {

        @Override
        public String getMessage() {
            return "Not Match AWS Account ID and Key";
        }
    },
    /* Azure 계정 관련 */
    INVALID_AZURE_ACCOUNT(310) {

        @Override
        public String getMessage() {
            return "Invalid Azure Account";
        }
    },
    INVALID_AZURE_EA_ACCOUNT(311) {

        @Override
        public String getMessage() {
            return "Invalid Azure EA Account";
        }
    },
    INVALID_AZURE_SUBSCRIPTION(312) {

        @Override
        public String getMessage() {
            return "Invalid Azure Subscription";
        }
    },
    /* ALIBABA 계정 관련 */
    INVALID_ALIBABA_ACCOUNT(320) {

        @Override
        public String getMessage() {
            return "Invalid Alibaba Account";
        }
    },
    NOT_MATCH_ALIBABA_ACCOUNT_ID_KEY(321) {

        @Override
        public String getMessage() {
            return "Not Match Alibaba Account ID and Key";
        }
    },
    /* AWS Cross Account  */
    AWS_CROSS_ACCOUNT_NOT_REGISTRATION(350) {

        @Override
        public String getMessage() {
            return "AWS Cross Account Not Registration";
        }
    },
    /* AWS Cross Account  */
    UNSUPPORTED_RESOURCE(351) {

        @Override
        public String getMessage() {
            return "Unsupported AWS Resource. Supported resource is ec2,ebs,rds,s3";
        }
    },
    /* 400 */
    INVALID_AUTHORITY(406) {

        @Override
        public String getMessage() {
            return "Invalid Authority";
        }
    },
    MISSING_PARAMETER(412) {

        @Override
        public String getMessage() {
            return "Missing parameter";
        }
    },
    INVALID_PARAMETER(416) {

        @Override
        public String getMessage() {
            return "Invalid parameter";
        }
    },
    MISSING_VER(417) {

        @Override
        public String getMessage() {
            return "Missing Required Header - ver";
        }
    },
    INVALID_VER(418) {

        @Override
        public String getMessage() {
            return "Invalid ver";
        }
    },
    INVALID_EMAIL_PARAMETER(420) {

        @Override
        public String getMessage() {
            return "Invalid Email Format";
        }
    },
    INVALID_USERNAME_PARAMETER(421) {

        @Override
        public String getMessage() {
            return "Invalid User Name";
        }
    },
    INVALID_CONFIRM_CODE(422) {

        @Override
        public String getMessage() {
            return "Invalid User Mobile Confirm Code";
        }
    },
    INVALID_COMPANYNAME_PARAMETER(423) {

        @Override
        public String getMessage() {
            return "Invalid Company Name";
        }
    },
    NOT_FOUND_COMPANY(424) {

        @Override
        public String getMessage() {
            return "Not Found Company";
        }
    },
    ALREADY_EXIST_USER_EMAIL(450) {

        @Override
        public String getMessage() {
            return "Already Exist User Email";
        }
    },
    ALREADY_INVITE_USER_EMAIL(451) {

        @Override
        public String getMessage() {
            return "Already Invite User Email";
        }
    },
    ALREADY_USER_PASSWORD_HISTORY(452) {

        @Override
        public String getMessage() {
            return "Already User Password History";
        }
    },
    NOT_FOUND_USER_EMAIL(452) {

        @Override
        public String getMessage() {
            return "Not Found User Email";
        }
    },
    NOT_FOUND_USER(453) {

        @Override
        public String getMessage() {
            return "Not Found User";
        }
    },
    CANCEL_INVITE_USER(454) {

        @Override
        public String getMessage() {
            return "Cancel Invite User";
        }
    },
    INSUFFICIENT_AUTHENTICATION(455) {

        @Override
        public String getMessage() {
            return "Insufficient authentication";
        }
    },
    ALREADY_EXIST_BILL_INFO(460) {

        @Override
        public String getMessage() {
            return "Already Exist Bill Info";
        }
    },
    ALREADY_EXIST_COMPANY_INFO(461) {

        @Override
        public String getMessage() {
            return "Already Company Info";
        }
    },

    NOT_EXIST_SYS_TYPE_CD(462) {

        @Override
        public String getMessage() {
            return "System Type Code Not Null";
        }
    },
    NOT_EXIST_USER_TYPE_CD(463) {

        @Override
        public String getMessage() {
            return "User Type Code is Undefined";
        }
    },
    BAD_REQUEST(464) {

        @Override
        public String getMessage() {
            return "Bad request";
        }
    },

    NOT_FOUND_USAGE(465) {

        @Override
        public String getMessage() {
            return "Usage not found";
        }
    },

    INVALID_ADMIN_USER(470) {
        @Override
        public String getMessage() {
            return "Invalid Admin User";
        }
    },
    NO_MATCHING_RESOURCE_TYPE(471) {
        @Override
        public String getMessage() {
            return "No matching resource details!";
        }
    },

    /* 500 */
    INTERNAL_ERROR(500) {

        @Override
        public String getMessage() {
            return HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        }
    },
    DATA_EMPTY(520) {

        @Override
        public String getMessage() {
            return "Data empty";
        }
    },
    INSERT_FAILED(521) {

        @Override
        public String getMessage() {
            return "Insert failed";
        }
    },
    UPDATE_FAILED(522) {

        @Override
        public String getMessage() {
            return "Update failed";
        }
    },
    DELETE_FAILED(523) {

        @Override
        public String getMessage() {
            return "Delete failed";
        }
    },
    ITEM_DUPLICATION(526) {

        @Override
        public String getMessage() {
            return "Item duplication";
        }
    },
    SEND_FAILED(550) {

        @Override
        public String getMessage() {
            return "The email was not sent.";
        }
    },
    SMS_SEND_FAILED(551) {

        @Override
        public String getMessage() {
            return "The sms was not sent.";
        }
    },

    /* 6000 : common token error code */
    MISSING_ATOKEN(6000) {

        @Override
        public String getMessage() {
            return "Missing Required Header - atoken";
        }
    },
    NOT_EXIST_TOKEN(6001) {

        @Override
        public String getMessage() {
            return "Not Exist Token";
        }
    },
    INVALID_ATOKEN(6002) {

        @Override
        public String getMessage() {
            return "Invalid atoken";
        }
    },
    AZURE_API_REQUEST_FAILED(6003) {

        @Override
        public String getMessage() {
            return "Azure REST API request failed";
        }
    },
    INVALID_AZURE_TOKEN(6004) {

        @Override
        public String getMessage() {
            return "Invalid Azure token";
        }
    },
    INVALID_UPLOAD_FILE_EXTENSION(600) {

        @Override
        public String getMessage() {
            return "Not allowed file extension";
        }
    },
    MISMATCH_SAAS_PRODUCE_CODE(700) {

        @Override
        public String getMessage() {
            return "SaaS product code mismatch";
        }
    },
    DUPLICATE_SAAS_ACCOUNT(701) {

        @Override
        public String getMessage() {
            return "Duplicate Saas Account";
        }
    },
    UNSUBSCRIBE_PENDING_ACCOUNT(702) {

        @Override
        public String getMessage() {
            return "Your subscription is currently being canceled. Please subscribe again later.";
        }
    },

    UNSUPPORTED_VENDORS_AND_RESOURCE_TYPES(900) {

        @Override
        public String getMessage() {
            return "Vendor and resource types of that type are not supported.";
        }
    },
    UNHANDLED_ERROR(999) {

        @Override
        public String getMessage() {
            return "Unhandled Error";
        }
    },;

    private final int code;

    ResponseSpecification(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
