package com.mcmp.gcpcollector.dao;

import com.mcmp.gcpcollector.dto.GcpUnusedVmDto;
import com.mcmp.gcpcollector.dto.GcpVmRightSizeDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GcpUnusedDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<GcpUnusedVmDto> selectYesterdayAvgCpuByVm() {
        return sqlSessionTemplate.selectList("gcp.selectYesterdayAvgCpuByVm");
    }

    public void insertUnusedDailyMart(GcpUnusedVmDto dto) {
        sqlSessionTemplate.insert("gcp.insertUnusedDailyMart", dto);
    }

    public GcpUnusedVmDto select14DaysMetricStats(String vmId) {
        return sqlSessionTemplate.selectOne("gcp.select14DaysMetricStats", vmId);
    }

    public void insertUnusedBatchRst(GcpUnusedVmDto dto) {
        sqlSessionTemplate.insert("gcp.insertUnusedBatchRst", dto);
    }

    public List<GcpVmRightSizeDto> selectRightSizeCandidates() {
        return sqlSessionTemplate.selectList("gcp.selectRightSizeCandidates");
    }

    public int checkTodayUnused(String vmId) {
        return sqlSessionTemplate.selectOne("gcp.checkTodayUnused", vmId);
    }
}
