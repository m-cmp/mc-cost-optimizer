package com.mcmp.costselector.unused.service;

import com.mcmp.costselector.model.util.AlarmReqModel;
import com.mcmp.costselector.unused.dao.UnusedSelectDao;
import com.mcmp.costselector.unused.model.*;
import com.mcmp.costselector.util.service.AlarmService;
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

    @Autowired
    private AlarmService alarmService;

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

//                    20240927. network asset -> 3차년도 예정
//        AssetMartReqModel networkMart = AssetMartReqModel.builder()
//                .cur_date(curDate)
//                .setting_value(5)
//                .setting_period(4)
//                .resource_id(rscStatus.getResource_id())
//                .metric_type("network")
//                .build();
//        NetworkAssetMartModel netRst = unusedSelectDao.getNetworkMart(networkMart);

        String resizingType;
//                    20240927. network asset -> 3차년도 예정
//        if(cpuRst != null && 4 <= cpuRst.getC_total_count() || netRst != null && 4 <= netRst.getN_total_count()){
        if(cpuRst != null && 4 <= cpuRst.getC_total_count()){
            if(cpuRst != null && cpuRst.getAvg_amount() > 80){
                resizingType = "Up";
                rscStatus.setIsUpsizeTarget(true);
                rscStatus.setIsDownsizeTarget(false);
            }
//                    20240927. network asset -> 3차년도 예정
//            else if(cpuRst != null && (cpuRst.getAvg_amount() <= 10 || cpuRst.getMax_amount() <= 50) || netRst != null && netRst.getAvg_amount() <= 5){
            else if(cpuRst != null && (cpuRst.getAvg_amount() <= 10 || cpuRst.getMax_amount() <= 50)){
                resizingType = "Down";
                rscStatus.setIsUpsizeTarget(false);
                rscStatus.setIsDownsizeTarget(true);
            } else {
                resizingType = "Modernize";
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

                OptiEC2SizeRstModel rcmdType = null;
                switch (resizingType){
                    case "Up":
                    case "Down":
                        rcmdType = unusedSelectDao.getRscEc2OptiSize(paramMap);
                        break;
                    case "Modernize":
                        rcmdType = unusedSelectDao.getRscEc2ModernizeType(paramMap);
                        break;
                }

                InstOptiRcmdRst rcmdRst;
                String alarmNote;
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
                    alarmNote = "인스턴스(" + rscStatus.getResource_id() + ")를 기존 타입 : "
                            + rscStatus.getInstance_type() + "에서 추천 타입 : " + rcmdType.getInstType() + "으로 변경하는 것을 추천드립니다.";
                } else if(rcmdType == null && !"Modernize".equals(resizingType)) {
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
                    alarmNote = "인스턴스(" + rscStatus.getResource_id() + ")를 기존 타입 : "
                            + rscStatus.getInstance_type() + "에서 " + resizingType + "Sizing으로 변경하는 것을 추천드립니다.";
                } else { // resizingType이 Modernize 이면서 추천 타입이 없을 때
                    rcmdRst = null;
                    alarmNote = "";
                    log.info("Instance size recommendation results : Modernize - Recommend type is None => Resource id : " + rscStatus.getResource_id());
                }

                if(rcmdRst != null){
                    log.info("Instance size recommendation results : " + rcmdRst);
                    unusedSelectDao.insertInstOptiRcmd(rcmdRst);

                    AlarmReqModel alarmReqModel = AlarmReqModel.builder()
                            .event_type("Resize")
                            .resource_id(rscStatus.getResource_id())
                            .resource_type(rscStatus.getRsrc_type())
                            .csp_type(rscStatus.getCsp_type())
                            .account_id(rscStatus.getCsp_account())
                            .urgency("Advise")
                            .plan(resizingType)
                            .note(alarmNote)
                            .build();

                    alarmService.sendAlarm(alarmReqModel);
                }

            }
        }

        return resizingType;
    }

}
