package com.mcmp.dummybe.model.login;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CurrentLoginModel {
    private String userId;
    private String changeUserGrpIdYn;
    private String userGrpId;
    private String userGrpTypeCd;
    private String siteCd;
    private String mngrMappingYn;
    private String userEmail;
    private String userNm;
    private String userDeptNm;
    private String userPstNm;
    private Date userPwdUpdtDt;
    private String blntCmpnNm;
    private String userMblTelCntrCd;
    private String userMblTelNo;
    private String userTelCntrCd;
    private String userTelNo;
    private String userStatCd;
    private String userStatNm;
    private String blntCmpnId;
    private String blntCmpnTypeCd;
    private String blntCmpnTypeNm;
    private String curCmpnId;
    private String curCmpnNm;
    private String userLangCd;
    private String updtId;
    private String updtIpaddr;
    private LocalDateTime creaDt;
    private LocalDateTime updtDt;
    private String pwdMustChangeYn;
    private String timezoneOffset;
    private String trialYn;
    private String trialExpireDt;
    private String trialRemainDays;
    private String chargeType;
    private String userGuideDelYn;
    private Boolean useConcurrent;
    private Boolean currentCustomerOrderd;

}