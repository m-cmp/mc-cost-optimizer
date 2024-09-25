package com.mcmp.costselector.unused.service;

import com.mcmp.costselector.unused.dao.UnusedSelectDao;
import com.mcmp.costselector.unused.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OptiSizeService {

    @Autowired
    private UnusedSelectDao unusedSelectDao;

    public String analyzeTypeForResizing(UnusedResourceStatusModel rscStatus){
        LocalDate curDate = ZonedDateTime.now().toLocalDate();

        AssetMartReqModel cpuMart = AssetMartReqModel.builder()
                .cur_date(curDate)
                .setting_value(3)
                .setting_period(4)
                .resource_id(rscStatus.getResource_id())
                .metric_type("cpu")
                .build();
        CpuAssetMartModel cpuRst = unusedSelectDao.getCPUAssetMart(cpuMart);

        AssetMartReqModel networkMart = AssetMartReqModel.builder()
                .cur_date(curDate)
                .setting_value(5)
                .setting_period(4)
                .resource_id(rscStatus.getResource_id())
                .metric_type("network")
                .build();
        NetworkAssetMartModel netRst = unusedSelectDao.getNetworkMart(networkMart);

        String resizingType;
        if(cpuRst != null && 4 <= cpuRst.getC_total_count() || netRst != null && 4 <= netRst.getN_total_count()){
            if(cpuRst != null && cpuRst.getAvg_amount() > 80){
                resizingType = "Up";
                rscStatus.setIsUpsizeTarget(true);
                rscStatus.setIsDownsizeTarget(false);
            } else if(cpuRst != null && (cpuRst.getAvg_amount() <= 10 || cpuRst.getMax_amount() <= 50) || netRst != null && netRst.getAvg_amount() <= 5){
                resizingType = "Down";
                rscStatus.setIsUpsizeTarget(false);
                rscStatus.setIsDownsizeTarget(true);
            } else {
                resizingType = "None";
            }
        } else {
            resizingType = "None";
        }


        if(!"None".equals(resizingType)){

            OptiSizeTargetMetaModel targetMeta = unusedSelectDao.getOptiSizeTargetMeta(rscStatus);
            if(targetMeta != null){

                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("rscInfo", rscStatus);
                paramMap.put("targetMeta", targetMeta);

                OptiEC2SizeRstModel rcmdType = unusedSelectDao.getRscEc2OptiSize(paramMap);
                InstOptiRcmdRst rcmdRst;
                if(rcmdType != null){
                    rcmdRst = InstOptiRcmdRst.builder()
                            .createDT(ZonedDateTime.now().toLocalDate())
                            .resourceID(rscStatus.getResource_id())
                            .cspType(rscStatus.getCsp_type())
                            .cspAccount(rscStatus.getCsp_account())
                            .originType(rscStatus.getInstance_type())
                            .rcmdType(rcmdType.getInstType())
                            .planType(resizingType)
                            .originUSD(targetMeta.getUsd())
                            .rcmdUSD(rcmdType.getUsd())
                            .build();
                } else{
                    rcmdRst = InstOptiRcmdRst.builder()
                            .createDT(ZonedDateTime.now().toLocalDate())
                            .resourceID(rscStatus.getResource_id())
                            .cspType(rscStatus.getCsp_type())
                            .cspAccount(rscStatus.getCsp_account())
                            .originType(rscStatus.getInstance_type())
                            .rcmdType("None")
                            .planType(resizingType)
                            .originUSD(targetMeta.getUsd())
                            .rcmdUSD(null)
                            .build();
                }
                log.info("Instance size recommendation results : " + rcmdRst);
                unusedSelectDao.insertInstOptiRcmd(rcmdRst);
            }
        }

        return resizingType;
    }

}
