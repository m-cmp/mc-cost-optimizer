package com.mcmp.costbe.tumblebugMeta.dao;

import com.mcmp.costbe.tumblebugMeta.model.ResourcegroupMetaModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class TBBDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public void insertTBBServicegroupMeta(List<ResourcegroupMetaModel> rscMetas){
        sqlSession.insert("tbb.insertTBBServicegroupMeta", rscMetas);
    }

    /** 현재 DB에 테이블이 있는지 (월별 CUR 테이블 tbl_table_billing_detail_YYYYMM 확인용) */
    public boolean existsTable(String tableName){
        Integer cnt = sqlSession.selectOne("tbb.countTable", tableName);
        return cnt != null && cnt > 0;
    }

    /** 해당 월 CUR에서 EKS 클러스터 ARN 후보 조회. yearMonth는 호출 측에서 YYYYMM 숫자 6자리로만 넘긴다. */
    public List<String> selectAwsEksArnsFromCur(String yearMonth, String arnLikePattern){
        return sqlSession.selectList("tbb.selectAwsEksArnsFromCur", Map.of("yearMonth", yearMonth, "pattern", arnLikePattern));
    }

    /** 같은 AWS 클러스터가 예전에 ARN이 아닌 값(클러스터 이름)으로 저장된 K8S 행 정리 */
    public int deleteStaleAwsK8sMeta(ResourcegroupMetaModel arnRow){
        return sqlSession.delete("tbb.deleteStaleAwsK8sMeta", arnRow);
    }


}
