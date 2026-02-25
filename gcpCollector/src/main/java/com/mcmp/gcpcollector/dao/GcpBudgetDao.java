package com.mcmp.gcpcollector.dao;

import com.mcmp.gcpcollector.dto.GcpBudgetUsageDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GcpBudgetDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<GcpBudgetUsageDto> selectGcpBudgetExceededProjects() {
        return sqlSessionTemplate.selectList("gcp.selectGcpBudgetExceededProjects");
    }
}
