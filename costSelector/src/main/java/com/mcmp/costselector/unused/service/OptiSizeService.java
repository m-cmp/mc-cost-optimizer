package com.mcmp.costselector.unused.service;

import com.mcmp.costselector.client.TumblebugClient;
import com.mcmp.costselector.model.util.AlarmReqModel;
import com.mcmp.costselector.unused.dao.UnusedSelectDao;
import com.mcmp.costselector.unused.model.*;
import com.mcmp.costselector.util.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class OptiSizeService {

    @Autowired
    private UnusedSelectDao unusedSelectDao;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private TumblebugClient tumblebugClient;

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

            // 현재 스펙/단가를 Tumblebug에서 직접 조회 (TASM 메타표 의존 제거).
            // originUSD(현재 스펙 시간당 USD) = Tumblebug costPerHour. 기존 TASM APP.USD 와 동일 단위.
            tumblebugClient.fillCurrentSpec(rscStatus);
            Double originUSD = rscStatus.getCurrentCostPerHour();

            {
                OptiEC2SizeRstModel rcmdType = null;
                switch (resizingType){
                    case "Up":
                    case "Down":
                        String tbbSpecName = tumblebugClient.recommendSpec(rscStatus, resizingType);
                        if (tbbSpecName != null) {
                            rcmdType = new OptiEC2SizeRstModel();
                            rcmdType.setInstType(tbbSpecName);
                            if ("Down".equals(resizingType)) {
                                boolean currentMissing   = rscStatus.getCurrentCostPerHour()   == null;
                                boolean recommendMissing = rscStatus.getRecommendCostPerHour() == null;
                                if (currentMissing && recommendMissing) {
                                    log.warn("Down 절감액 계산 불가 - 현재/추천 스펙 단가 모두 미제공, resourceId: {}", rscStatus.getResource_id());
                                } else if (currentMissing) {
                                    log.warn("Down 절감액 계산 불가 - 현재 스펙 단가 미제공, resourceId: {}", rscStatus.getResource_id());
                                } else if (recommendMissing) {
                                    log.warn("Down 절감액 계산 불가 - 추천 스펙 단가 미제공, resourceId: {}", rscStatus.getResource_id());
                                } else {
                                    double savings = (rscStatus.getCurrentCostPerHour() - rscStatus.getRecommendCostPerHour()) * 24 * 30;
                                    if (savings > 0) rcmdType.setUsd(savings);
                                }
                            }
                        }
                        break;
                    case "Modernize":
                        String modernizeSpecName = tumblebugClient.findModernizeSpec(rscStatus);
                        if (modernizeSpecName != null) {
                            rcmdType = new OptiEC2SizeRstModel();
                            rcmdType.setInstType(modernizeSpecName);
                        }
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
                            .originUSD(originUSD)
                            .rcmdUSD(rcmdType.getUsd())
                            .build();
                    alarmNote = "Recommend resizing instance (" + rscStatus.getResource_id() + ") from current type "
                            + rscStatus.getInstance_type() + " to recommended type " + rcmdType.getInstType() + ".";
                } else if(rcmdType == null && !"Modernize".equals(resizingType)) {
                    rcmdRst = InstOptiRcmdRst.builder()
                            .createDT(ZonedDateTime.now().toLocalDate())
                            .resourceID(rscStatus.getResource_id())
                            .cspType(rscStatus.getCsp_type())
                            .cspAccount(rscStatus.getCsp_account())
                            .originType(rscStatus.getInstance_type())
                            .rcmdType("None")
                            .planType(resizingType)
                            .originUSD(originUSD)
                            .rcmdUSD(null)
                            .build();
                    alarmNote = "Recommend " + resizingType + " sizing for instance (" + rscStatus.getResource_id() + ") from current type "
                            + rscStatus.getInstance_type() + ".";
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
                            .project_cd(rscStatus.getService_cd() != null ? rscStatus.getService_cd() : "default")
                            .build();

                    alarmService.sendAlarm(alarmReqModel);
                }

            }
        }

        return resizingType;
    }

}
