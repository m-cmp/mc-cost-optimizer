package com.mcmp.costselector.unused.service;

import com.mcmp.costselector.unused.dao.UnusedSelectDao;
import com.mcmp.costselector.unused.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class UnusedSelectService {

    @Autowired
    private UnusedSelectDao unusedSelectDao;


    public void unusedResourceSelector(UnusedSelectReqModel req){
        try{

            // todo. cmp 유저 정보 <-> csp account mapping (query)
            UnusedResourceStatusModel rsStatus = unusedSelectDao.getResourceStatus(req);
            String plan = "None";
            if(rsStatus != null && (!rsStatus.getResource_status().isEmpty() || !rsStatus.getResource_spot_yn().isEmpty())){
                if("running".equals(rsStatus.getResource_status()) && "N".equals(rsStatus.getResource_spot_yn())){
                    LocalDate curDate = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDate();

                    List<UserAssetRSOPTModel> userAssetSetting = unusedSelectDao.getUserAssetRSOPT(rsStatus.getCmp_user_id());

                    UserAssetRSOPTModel cpuSet = userAssetSetting.stream().filter(item -> "CPU".equals(item.getMetric_type()))
                            .findFirst().orElse(null);
                    AssetMartReqModel cpuMart = AssetMartReqModel.builder()
                            .cur_date(curDate)
                            .setting_value(cpuSet != null ? cpuSet.getCriteria_value() : 3)
                            .setting_period(cpuSet != null ? cpuSet.getRegress_duration() : 14)
                            .resource_id(req.getResource_id())
                            .metric_type("cpu")
                            .build();
                    CpuAssetMartModel cpuRst = unusedSelectDao.getCPUAssetMart(cpuMart);

                    UserAssetRSOPTModel networkSet = userAssetSetting.stream().filter(item -> "NETWORK".equals(item.getMetric_type()))
                            .findFirst().orElse(null);
                    AssetMartReqModel networkMart = AssetMartReqModel.builder()
                            .cur_date(curDate)
                            .setting_value(networkSet != null ? networkSet.getCriteria_value() : 5)
                            .setting_period(networkSet != null ? networkSet.getRegress_duration() : 14)
                            .resource_id(req.getResource_id())
                            .metric_type("network")
                            .build();
                    NetworkAssetMartModel netRst = unusedSelectDao.getNetworkMart(networkMart);
                    int refVal = (int) Math.ceil(Math.abs(networkMart.getSetting_period() +1 / 4));

                    if(cpuMart.getSetting_period() <= cpuRst.getC_total_count()
                            && ("TRUE".equals(cpuRst.getMax_amount())
                            || 1 > cpuRst.getAvg_amount()) ){
                        plan = "Unused";
                    } else if (networkMart.getSetting_period() <= netRst.getN_total_count()
                            && (netRst.getCount_less_than_setting() >= refVal
                            || netRst.getCount_occured() < refVal)){
                        plan = "Unused";
                    } else if (cpuMart.getSetting_period() > cpuRst.getC_total_count() && networkMart.getSetting_period() > netRst.getN_total_count()) {
                        plan = "None";
                    } else{
                        plan = "Down";
                    }
                } else {
                    plan = "None";
                }

                UnusedBatchRstModel rstModel = UnusedBatchRstModel.builder()
                        .create_dt(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDate().atStartOfDay())
                        .csp_type(rsStatus.getCsp_type())
                        .csp_account(rsStatus.getCsp_account())
                        .csp_instanceid(rsStatus.getResource_id())
                        .plan_type(plan)
                        .build();
                log.info("리소스 선별 결과 : " + rstModel);
                unusedSelectDao.insertBatchRst(rstModel);
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("Error -> unusedResourceSelector : ", e.getMessage());
        }
    }

}
