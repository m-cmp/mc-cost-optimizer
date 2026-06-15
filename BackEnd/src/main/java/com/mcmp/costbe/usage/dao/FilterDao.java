package com.mcmp.costbe.usage.dao;

import com.mcmp.costbe.usage.model.bill.BillingWidgetModel;
import com.mcmp.costbe.usage.model.filter.ProjectsModel;
import com.mcmp.costbe.usage.model.filter.WorkspacesModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilterDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public List<WorkspacesModel> getWorkspaces(){
        return sqlSession.selectList("bill.getWorkspaces");
    }

    public List<ProjectsModel> getProjects() {
        return sqlSession.selectList("bill.getProjects");
    }

}
