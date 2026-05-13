package com.mcmp.gcpcollector.dao;

import com.mcmp.gcpcollector.dto.GcpAnomalyDto;
import com.mcmp.gcpcollector.dto.GcpProjectCostAnalysisDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class GcpAnomalyDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<GcpProjectCostAnalysisDto> getGcpAbnormalCosts(Map<String, Object> param) {
        return sqlSessionTemplate.selectList("gcp.getGcpAbnormalCosts", param);
    }

    public void insertDailyAbnormal(GcpAnomalyDto dto) {
        sqlSessionTemplate.insert("gcp.insertDailyAbnormal", dto);
    }
}
