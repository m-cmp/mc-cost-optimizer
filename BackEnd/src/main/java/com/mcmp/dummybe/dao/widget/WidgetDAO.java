package com.mcmp.dummybe.dao.widget;

import com.mcmp.dummybe.model.widget.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class WidgetDAO {
    @Resource(name="sqlSessionTemplateCostOptimize")
    private SqlSessionTemplate sqlSession;

    public List<WidgetTrendDailyCostModel> selectTrendDaily(){
        return sqlSession.selectList("cost_optimize_widget_trend.selectTrendDaily");
    }

    public List<WidgetTop5CostModel> selectTop5Cost() {
        return sqlSession.selectList("cost_optimize_widget_top5.selectTop5Cost");
    }

    public List<WidgetAbnormalListModel> getAbnormalList(){
        return sqlSession.selectList("cost_optimize_widget.abnormalList");
    }

    public WidgetAbnormalDataModel getAbnormalSummary(){
        return sqlSession.selectOne("cost_optimize_widget.abnormalSummary");
    }

    public List<WidgetCostListModel> getCostList(){
        return sqlSession.selectList("cost_optimize_widget.costList");
    }

    public List<WidgetCostCustomFilterModel> getCostCustomFilters(){
        return sqlSession.selectList("cost_optimize_widget.costCustomFilters");
    }

    public List<WidgetProductPortionAccountModel> getProductPortionAccount() {
        return sqlSession.selectList("cost_optimize_widget_product_portion.selectProductPortionAccount");
    }
    public List<WidgetProductPortionTimeFrameModel> getProductPortionTimeFrame() {
        return sqlSession.selectList("cost_optimize_widget_product_portion.selectProductPortionTimeFrame");
    }
    public List<WidgetProductPortionItemsModel> getProductPortionItem() {
        return sqlSession.selectList("cost_optimize_widget_product_portion.selectProductPortionItem");
    }
    public List<WidgetProductPortionPortionModel> getProductPortionFamily() {
        return sqlSession.selectList("cost_optimize_widget_product_portion.selectProductPortionFamily");
    }
}
