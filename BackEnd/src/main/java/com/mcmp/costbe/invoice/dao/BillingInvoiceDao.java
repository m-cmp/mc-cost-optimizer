package com.mcmp.costbe.invoice.dao;

import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoReqModel;
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
