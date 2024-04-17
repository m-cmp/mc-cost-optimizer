package com.mcmp.dummybe.service.common;

import com.mcmp.dummybe.dao.common.CommonDAO;
import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.model.common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommonService {
    @Autowired
    private CommonDAO commonDAO;

    public ResultModel vendorStatus(){
        ResultModel result = new ResultModel();
        List<VendorStatusModel> resultData = commonDAO.vendorStatus();

        result.setResult(resultData);
        return result;
    }

    public ResultModel vendorList(){
        ResultModel result = new ResultModel();
        Map<String, List> resultData = new HashMap<>();
        resultData.put("vendors", VendorListModel.vendors);

        result.setData(resultData);
        return result;
    }

    public ResultModel userInfo(){
        ResultModel result = new ResultModel();
        UserInfoModel userInfoModel = new UserInfoModel();
        List awsCurrenciesList = new ArrayList<>();
        Map<String, String> awsCurrencies = new HashMap<>();
        awsCurrencies.put("siteCode", null);
        awsCurrencies.put("companyId", "1");
        awsCurrencies.put("vendor", null);
        awsCurrencies.put("currency", null);
        awsCurrencies.put("rate", "1.0");
        awsCurrencies.put("billYear", "2023");
        awsCurrencies.put("billMonth", "08");

        awsCurrenciesList.add(awsCurrencies);
        userInfoModel.getVendorCurrencies().replace("aws", awsCurrenciesList);
        result.setData(userInfoModel);
        return result;
    }

    public ResultModel getCompanyVendor(){
        ResultModel result = new ResultModel();
        List<CompanyVendorModel> companyVendorModels = commonDAO.vendorAccountList();

        CompanyListModel companyList = new CompanyListModel(companyVendorModels);

        result.setResult(companyList);
        return result;
    }
}
