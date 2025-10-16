package com.mcmp.costbe.invoice.dao;

import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoReqModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BillingInvoiceDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public List<BillingInvoiceBaseInfoModel> getCurMonthBill(BillingInvoiceBaseInfoReqModel req){
        return sqlSession.selectList("bill.getCurMonthBill", req);
    }
}
