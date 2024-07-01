package com.mcmp.costbe.usage.dao;

import com.mcmp.costbe.usage.model.bill.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BillDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public BillingWidgetModel getCurPrevMonthBill(BillingWidgetReqModel req){
        return sqlSession.selectOne("bill.getCurPrevMonthBill", req);
    }

    public List<MonthlyBillModel> getMonthBill(BillingWidgetReqModel req){
        return sqlSession.selectList("bill.getMonthBill", req);
    }

    public List<Top5BillModel> getTop5Bill(Top5WidgetReqModel req){
        return sqlSession.selectList("bill.getTop5Bill", req);
    }

    public List<BillingAssetChildModel> getBillAssetChild(BillingAssetReqModel req){
        return sqlSession.selectList("bill.getBillAssetChild", req);
    }
}
