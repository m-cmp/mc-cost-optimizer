package com.mcmp.gcpcollector.config;

import com.mcmp.gcpcollector.dao.GcpBillingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GcpCollectorInitializer {

    private final GcpBillingDao gcpBillingDao;

    @EventListener(ApplicationReadyEvent.class)
    public void initTable() {
        log.info("gcp_billing_raw 테이블 초기화");
        try {
            gcpBillingDao.initTable();
            log.info("gcp_billing_raw 테이블 준비 완료");
        } catch (Exception e) {
            log.error("gcp_billing_raw 테이블 초기화 실패 - DB 연결 및 DDL 실행 권한을 확인하세요. url: {}",
                    System.getProperty("spring.datasource.url", "(설정 없음)"), e);
            throw e;
        }
    }
}
