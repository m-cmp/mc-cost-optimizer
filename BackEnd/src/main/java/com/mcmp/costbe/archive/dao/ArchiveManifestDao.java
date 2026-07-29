package com.mcmp.costbe.archive.dao;

import com.mcmp.costbe.archive.model.ArchiveManifest;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** cost_archive_manifest CRUD. (mapper/archive/Archive_SQL.xml, namespace="archive") */
@Repository
public class ArchiveManifestDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public void upsert(ArchiveManifest m) {
        sqlSession.update("archive.upsertManifest", m);
    }

    public ArchiveManifest find(String yearMonth, String csp) {
        return sqlSession.selectOne("archive.findManifest", key(yearMonth, csp));
    }

    public List<ArchiveManifest> findAll() {
        return sqlSession.selectList("archive.findAllManifest");
    }

    public void markPurged(String yearMonth, String csp) {
        sqlSession.update("archive.markPurged", key(yearMonth, csp));
    }

    private Map<String, Object> key(String yearMonth, String csp) {
        Map<String, Object> p = new HashMap<>();
        p.put("yearMonth", yearMonth);
        p.put("csp", csp);
        return p;
    }
}
