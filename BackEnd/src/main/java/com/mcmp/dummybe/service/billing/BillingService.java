package com.mcmp.dummybe.service.billing;

import com.mcmp.dummybe.dao.billing.BillingDAO;
import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.model.billing.BillsDataModel;
import com.mcmp.dummybe.model.billing.ChargesDataModel;
import com.mcmp.dummybe.model.billing.DetailsDataModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BillingService {
    @Autowired
    BillingDAO billingDAO;

    public ResultModel billingDetail(){
        ResultModel result = new ResultModel();
        Map<String, List<DetailsDataModel>> resultData = new HashMap<>();
        List<DetailsDataModel> detailsData = billingDAO.billingDetail();
        resultData.put("Details", detailsData);

        result.setData(resultData);
        return result;
    }

    public ResultModel billingCharges(){
        ResultModel result = new ResultModel();
        Map<String, List<ChargesDataModel>> resultData = new HashMap<>();
        List chargesData = billingDAO.billingCharges();
        resultData.put("charges", chargesData);

        result.setData(resultData);
        return result;
    }

    public ResultModel billingBills(){
        ResultModel result = new ResultModel();
        Map<String, List<BillsDataModel>> resultData = new HashMap<>();
        List<BillsDataModel> billsData = billingDAO.billingBills();
        resultData.put("bills", billsData);

        result.setData(resultData);
        return result;
    }

}
