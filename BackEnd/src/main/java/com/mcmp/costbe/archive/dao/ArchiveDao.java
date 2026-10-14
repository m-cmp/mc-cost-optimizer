package com.mcmp.costbe.archive.dao;

import com.mcmp.costbe.archive.model.UserArnModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 아카이브 공통 DAO (mapper/archive/Archive_SQL.xml, namespace="archive").
 * - manifest 테이블 부팅 자동 생성
 * - temp_cmp_user_role_arn 에서 버킷/역할 조회(재사용)
 */
@Repository
public class ArchiveDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    /** 부팅 시 manifest 테이블 보증 (CREATE TABLE IF NOT EXISTS). */
    public void initManifestTable() {
        sqlSession.update("archive.initManifestTable");
    }

    /** 아카이브 목적지 버킷 + AssumeRole role_arn (CUR 접근 정보 재사용). */
    public UserArnModel getAwsUserArn() {
        return sqlSession.selectOne("archive.getAwsUserArn");
    }

    /** 아카이브 가능한 연·월(각 CSP 소스 합집합, 최신순). */
    public java.util.List<String> getArchivableMonths() {
        return sqlSession.selectList("archive.getArchivableMonths");
    }

    /** AWS 해당 월이 확정(certifed_fixed_yn 전부 'Y')됐는지. */
    public boolean isAwsMonthFinalized(String yearMonth) {
        Boolean b = sqlSession.selectOne("archive.isAwsMonthFinalized", yearMonth);
        return Boolean.TRUE.equals(b);
    }
}
