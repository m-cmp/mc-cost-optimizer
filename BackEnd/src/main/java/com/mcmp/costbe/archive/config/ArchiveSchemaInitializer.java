package com.mcmp.costbe.archive.config;

import com.mcmp.costbe.archive.dao.ArchiveDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 부팅 시 cost_archive_manifest 테이블을 보증한다 (CREATE TABLE IF NOT EXISTS).
 * 콜렉터의 initTable / GcpCollectorInitializer 와 동일 패턴 — 있으면 스킵, 없으면 생성.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArchiveSchemaInitializer {

    private final ArchiveDao archiveDao;

    @PostConstruct
    public void init() {
        try {
            archiveDao.initManifestTable();
            log.info("[archive] cost_archive_manifest 테이블 준비 완료");
        } catch (Exception e) {
            log.error("[archive] cost_archive_manifest 초기화 실패: {}", e.getMessage(), e);
        }
    }
}
