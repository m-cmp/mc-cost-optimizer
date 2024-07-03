package com.mcmp.costbe.invoice.dao;

import com.mcmp.costbe.invoice.model.InvoiceItemModel;
import com.mcmp.costbe.invoice.model.InvoiceReqModel;
import com.mcmp.costbe.invoice.model.SummaryReqModel;
import com.mcmp.costbe.usage.model.bill.BillingWidgetModel;
import com.mcmp.costbe.usage.model.bill.BillingWidgetReqModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class InvoiceDao {
    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public Double getSummary(SummaryReqModel req){
        return sqlSession.selectOne("invoice.getAWSSummaryBill", req);
    }

    public List<InvoiceItemModel> getAWSInvoice(InvoiceReqModel req){
        return sqlSession.selectList("invoice.getAWSInovice", req);
    }
}
