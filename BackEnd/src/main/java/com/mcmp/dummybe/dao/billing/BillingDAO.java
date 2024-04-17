package com.mcmp.dummybe.dao.billing;

import com.mcmp.dummybe.model.billing.BillsDataModel;
import com.mcmp.dummybe.model.billing.ChargesDataModel;
import com.mcmp.dummybe.model.billing.DetailsDataModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BillingDAO {
    @Resource(name="sqlSessionTemplateCostOptimize")
    private SqlSessionTemplate sqlSession;

    public List<DetailsDataModel> billingDetail(){
        return sqlSession.selectList("cost-optimize-billing.details");
    }

    public List<ChargesDataModel> billingCharges(){
        return sqlSession.selectList("cost-optimize-billing.charges");
    }

    public List<BillsDataModel> billingBills(){
        return sqlSession.selectList("cost-optimize-billing.bills");
    }
}
