package com.mcmp.dummybe.model.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyListModel {
    private String siteCd = "MCMP";
    private String userId = "MCMP";
    private String userEmail = "MCMP@mcmp.com";
    private String userNm = "mcmpUser";
    private String userGrpId;
    private String userGrpNm;
    private String userGrpDesc;
    private String userGrpTypeCd = "USER_GRP_TYPE_010";
    private String userGrpTypeNm = "White Labeling Admin Group";
    private List<CmpnModel> cmpnList;

    public CompanyListModel(List<CompanyVendorModel> accVendorList){
        CmpnModel cmpnModel = new CmpnModel();
        cmpnModel.setVendor(accVendorList);

        List<CmpnModel> cmpnModelList = new ArrayList<>();
        cmpnModelList.add(cmpnModel);

        this.cmpnList = cmpnModelList;

    }


    @Data
    public class CmpnModel{
        private String cmpnId = "1";
        private String cmpnNm = "MCMP";
        private String cmpnUtcTmzn="+09:00";
        private String authYn;
        private String service;
        private List<CompanyVendorModel> vendor;

    }

}
