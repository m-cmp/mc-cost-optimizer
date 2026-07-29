package com.mcmp.collector.dao;

import com.mcmp.collector.model.aws.DataExportBucketModel;
import com.mcmp.collector.model.aws.UserArnModel;
import com.mcmp.collector.model.cur.AwsCurDetailModel;
import com.mcmp.collector.model.cur.AwsCurModel;
import com.mcmp.collector.model.cur.CurProcessModel;
import com.mcmp.collector.model.cur.RscGrpMetaModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AwsDao {

    @Autowired
    @Qualifier("sqlSessionTemplateSimple")
    private SqlSessionTemplate sqlSessionTemplate;

    public void createTable(String suffix){
        sqlSessionTemplate.update("aws.createTable", suffix);
    }

    public void dropTable(String suffix){
        sqlSessionTemplate.update("aws.dropTable", suffix);
    }

    public void insertCURProcess(CurProcessModel model){
        sqlSessionTemplate.insert("aws.insertCURProcess", model);
    }

    public List<CurProcessModel> getTodoCURCollectMonth(String account){
        return sqlSessionTemplate.selectList("aws.getTodoCURCollectMonth", account);
    }

    /** 해당 payer의 당월 todo 행이 이미 있는지 개수 조회(0이면 생성 필요). */
    public int countCurProcessMonth(String payer, String collectDate){
        Map<String, Object> param = new HashMap<>();
        param.put("payer", payer);
        param.put("collectDate", collectDate);
        Integer cnt = sqlSessionTemplate.selectOne("aws.countCurProcessMonth", param);
        return cnt == null ? 0 : cnt;
    }

    public List<String> getPayerID(){
        return sqlSessionTemplate.selectList("aws.getPayerID");
    }

    public UserArnModel getUserArn(String cmpUserId){
        return sqlSessionTemplate.selectOne("aws.getUserArn", cmpUserId);
    }

    public DataExportBucketModel getExportBucket(String cmpUserId){
        return sqlSessionTemplate.selectOne("aws.getDataExportBucket", cmpUserId);
    }

    public void insertCurOriginBatch(List<AwsCurModel> batchList){
        sqlSessionTemplate.insert("aws.insertCurOriginBatch", batchList);
    }

    public void insertCurDetailBatch(AwsCurDetailModel model){
        sqlSessionTemplate.insert("aws.insertCurDetailBatch", model);
    }

    public void insertRscGrpMeta(RscGrpMetaModel model){
        sqlSessionTemplate.insert("aws.insertRscGrpMeta", model);
    }

    public void insertMonthlySum(String yearmonth){
        sqlSessionTemplate.insert("aws.insertMonthlySum", yearmonth);
    }

    public void insertDailySumByProduct(String yearmonth){
        sqlSessionTemplate.insert("aws.insertDailySumByProduct", yearmonth);
    }

}
