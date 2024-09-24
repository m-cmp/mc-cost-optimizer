package com.mcmp.costbe.opti.service;

import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.opti.dao.OptiDao;
import com.mcmp.costbe.opti.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OptiService {

    @Autowired
    private DateCalculator dateCalculator;

    @Autowired
    private OptiDao optiDao;

    public UnusedRstModel getOptiUnused(UnusedReqModel req){
        try {
            UnusedRstModel rst = new UnusedRstModel();
            LocalDate curDate = dateCalculator.curLocalDate();
            LocalDate lastYM = curDate.minusMonths(1);

            UnusedQueryParamModel queryParam = new UnusedQueryParamModel();
            queryParam.setCurDate(curDate);
            queryParam.setLastYearMonth(lastYM.format(DateTimeFormatter.ofPattern("yyyyMM")));
            queryParam.setSelectedCsps(req.getSelectedCsps());
            queryParam.setSelectedProjects(req.getSelectedProjects());
            queryParam.setSelectedWorkspace(req.getSelectedWorkspace());

            List<UnusedQueryRstModel> queryRst = optiDao.getOptiUnused(queryParam);

            rst.setCurDate(curDate);
            rst.setSelectedCsps(req.getSelectedCsps());
            rst.setSelectedWorkspace(req.getSelectedWorkspace());
            rst.setSelectedProjects(req.getSelectedProjects());
            rst.setUnusedRec(queryRst);

            return rst;

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public AbnormalRstModel getOptiAbrnomal(AbnormalReqModel req){
        try{
            LocalDate date = LocalDate.now();
            req.setDate(date);

            AbnormalRstModel result = AbnormalRstModel.builder()
                    .today(date.toString())
                    .abnoramlItems(optiDao.getOptiAbnormal(req))
                    .selectedWorkspace(req.getSelectedWorkspace())
                    .selectedProjects(req.getSelectedProjects())
                    .selectedCsps(req.getSelectedCsps())
                    .build();

            return result;

        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public InstOptiSizeRstModel getInstOptiSizeRcmd(InstOptiSizeReqModel req){
        try{
            LocalDate date = LocalDate.now();
            req.setDate(date);

            InstOptiSizeRstModel result = InstOptiSizeRstModel.builder()
                    .today(date.toString())
                    .optiSizeItems(optiDao.getInstOptiSize(req))
                    .selectedWorkspace(req.getSelectedWorkspace())
                    .selectedProjects(req.getSelectedProjects())
                    .selectedCsps(req.getSelectedCsps())
                    .build();

            return result;

        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
