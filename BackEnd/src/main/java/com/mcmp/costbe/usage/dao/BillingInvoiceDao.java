package com.mcmp.costbe.usage.dao;

import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoReqModel;
import com.mcmp.costbe.usage.model.bill.BillingWidgetModel;
import com.mcmp.costbe.usage.model.bill.BillingWidgetReqModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class BillingInvoiceDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public BillingInvoiceBaseInfoModel getCurMonthBill(BillingInvoiceBaseInfoReqModel req){
        return sqlSession.selectOne("bill.getCurMonthBill", req);
    }
}
