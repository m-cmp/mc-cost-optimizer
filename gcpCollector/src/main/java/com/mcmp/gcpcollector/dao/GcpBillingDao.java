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

    /** usage_start_time 이 [start, end) 인 raw 행 삭제 (UTC "yyyy-MM-dd HH:mm:ss"). 같은 기간을 다시 적재하기 전에 호출 */
    public int deleteByUsageRange(String start, String end) {
        return sqlSessionTemplate.delete("gcp.deleteByUsageRange", java.util.Map.of("start", start, "end", end));
    }

    public void upsertMonthly(String invoiceMonth) {
        sqlSessionTemplate.insert("gcp.upsertMonthly", invoiceMonth);
    }

}
