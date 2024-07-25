package com.mcmp.costbe.opti.service;

import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.opti.dao.OptiDao;
import com.mcmp.costbe.opti.model.UnusedQueryParamModel;
import com.mcmp.costbe.opti.model.UnusedQueryRstModel;
import com.mcmp.costbe.opti.model.UnusedReqModel;
import com.mcmp.costbe.opti.model.UnusedRstModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
            LocalDate curDate = dateCalculator.curUTCLocalDate();

            UnusedQueryParamModel queryParam = new UnusedQueryParamModel();
            queryParam.setCurDate(curDate);
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
}
