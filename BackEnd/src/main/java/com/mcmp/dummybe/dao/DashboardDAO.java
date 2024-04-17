package com.mcmp.dummybe.dao;

import com.mcmp.dummybe.model.dashboard.DashboardUserDataModel;
import com.mcmp.dummybe.model.dashboard.DashboardWidgetDefaultValuesModel;
import com.mcmp.dummybe.model.dashboard.DashboardWidgetOptionsModel;
import com.mcmp.dummybe.model.dashboard.DashboardWidgetUserDataModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class DashboardDAO {
    @Resource(name="sqlSessionTemplateCostOptimize")
    private SqlSessionTemplate sqlSession;

    public List<String> selectDashboardKinds(){
        return sqlSession.selectList("cost_optimize_dashboards.selectDashboardKinds");
    }

    public List<DashboardWidgetOptionsModel> selectWidgetAvailableOpt(){
        return sqlSession.selectList("cost_optimize_dashboards.selectWidgetAvailableOpt");
    }

    public List<DashboardWidgetDefaultValuesModel> selectWidgetOptDefault(){
        return sqlSession.selectList("cost_optimize_dashboards.selectWidgetOptDefault");
    }
    public DashboardUserDataModel selectUserWidgetInfo(int dashboardIndex){
        return sqlSession.selectOne("cost_optimize_dashboards.selectUserWidgetInfo",dashboardIndex);
    }
    public List<DashboardWidgetUserDataModel> selectUserWidgetDetailData(int dashboardIndex){
        return sqlSession.selectList("cost_optimize_dashboards.selectUserWidgetDetailData",dashboardIndex);
    }
}
