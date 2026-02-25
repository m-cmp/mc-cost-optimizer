package com.mcmp.gcpcollector.config;

import com.google.cloud.bigquery.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
public class BigQueryConfig {

    @Value("${gcp.dataset:}")
    private String dataset;

    @Value("${gcp.table:}")
    private String table;

    private String projectId;

    @Bean
    public BigQuery bigQuery() {
        String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (credPath == null || credPath.isEmpty()) {
            log.error("===========================================");
            log.error("GOOGLE_APPLICATION_CREDENTIALS 환경변수가 설정되지 않았습니다.");
            log.error("설정 방법: export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account-key.json");
            log.error("===========================================");
            throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS 환경변수를 설정하세요.");
        }

        BigQuery bq = BigQueryOptions.getDefaultInstance().getService();

        this.projectId = bq.getOptions().getProjectId();
        log.info("BigQuery 연결 완료 - project: {}", projectId);

        // dataset/table 미지정 시 자동 탐색
        if (dataset == null || dataset.isEmpty() || table == null || table.isEmpty()) {
            autoDiscoverBillingTable(bq);
        }

        log.info("빌링 테이블: {}.{}.{}", projectId, dataset, table);
        return bq;
    }

    private void autoDiscoverBillingTable(BigQuery bq) {
        log.info("빌링 내보내기 테이블 자동 탐색 중...");

        try {
            for (Dataset ds : bq.listDatasets(projectId).iterateAll()) {
                String dsName = ds.getDatasetId().getDataset();

                for (Table tbl : bq.listTables(dsName).iterateAll()) {
                    String tblName = tbl.getTableId().getTable();

                    if (tblName.startsWith("gcp_billing_export")) {
                        this.dataset = dsName;
                        this.table = tblName;
                        log.info("빌링 테이블 발견: {}.{}", dsName, tblName);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("테이블 자동 탐색 실패: {}", e.getMessage());
        }

        log.warn("빌링 내보내기 테이블을 찾지 못했습니다. GCP_BQ_DATASET, GCP_BQ_TABLE 환경변수를 직접 설정하세요.");
    }
}
