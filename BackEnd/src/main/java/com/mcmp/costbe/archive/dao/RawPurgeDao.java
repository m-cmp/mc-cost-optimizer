package com.mcmp.costbe.archive.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * raw 삭제 SQL 실행 (게이트 통과 후에만 호출됨).
 * - NCP(3테이블)/Azure(2테이블)는 @Transactional 로 원자적 삭제 (프록시 경유이므로 트랜잭션 적용됨)
 * - AWS DROP TABLE 은 DDL(auto-commit) 이라 트랜잭션 불가 → DROP 후 cur_origin DELETE 별도
 */
@Repository
public class RawPurgeDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    // --- AWS ---
    /** ★ 동적 테이블명 DROP. yearMonth 는 \d{6} 만 허용(주입 방어). */
    public void dropAwsDetailTable(String yearMonth) {
        assertYm(yearMonth);
        sqlSession.update("archive.dropAwsDetailTable", param(yearMonth));
    }
    public int purgeAwsCurOrigin(String yearMonth) {
        assertYm(yearMonth);
        return sqlSession.delete("archive.purgeAwsCurOrigin", param(yearMonth));
    }

    // --- GCP ---
    public int purgeGcp(String yearMonth) {
        assertYm(yearMonth);
        return sqlSession.delete("archive.purgeGcp", param(yearMonth));
    }

    // --- NCP (3테이블 원자적) ---
    @Transactional
    public int purgeNcpAll(String yearMonth) {
        assertYm(yearMonth);
        int n = sqlSession.delete("archive.purgeNcpVmMonth", param(yearMonth));
        n += sqlSession.delete("archive.purgeNcpVmDaily", param(yearMonth));
        n += sqlSession.delete("archive.purgeNcpServiceMonth", param(yearMonth));
        return n;
    }

    // --- Azure (2테이블 원자적) ---
    @Transactional
    public int purgeAzureAll(String yearMonth) {
        assertYm(yearMonth);
        int n = sqlSession.delete("archive.purgeAzureVmDaily", param(yearMonth));
        n += sqlSession.delete("archive.purgeAzureServiceDaily", param(yearMonth));
        return n;
    }

    private void assertYm(String ym) {
        if (ym == null || !ym.matches("\\d{6}")) {
            throw new IllegalArgumentException("잘못된 yearMonth: " + ym);
        }
    }

    private java.util.Map<String, Object> param(String ym) {
        java.util.Map<String, Object> p = new java.util.HashMap<>();
        p.put("yearMonth", ym);
        return p;
    }
}
