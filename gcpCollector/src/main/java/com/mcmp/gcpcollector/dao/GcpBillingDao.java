package com.mcmp.gcpcollector.dao;

import com.mcmp.gcpcollector.dto.GcpBillingRawDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GcpBillingDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public void initTable() {
        sqlSessionTemplate.update("gcp.initTable");
    }

    public void insertBatch(List<GcpBillingRawDto> list) {
        sqlSessionTemplate.insert("gcp.insertBatch", list);
    }

    public void upsertMonthly(String invoiceMonth) {
        sqlSessionTemplate.insert("gcp.upsertMonthly", invoiceMonth);
    }

}
