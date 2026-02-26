package com.mcmp.gcpcollector.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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
    public BigQuery bigQuery() throws Exception {
        String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        String gcpProjectId = System.getenv("GCP_PROJECT_ID");
        String clientEmail = System.getenv("GCP_CLIENT_EMAIL");
        String privateKey = System.getenv("GCP_PRIVATE_KEY");
        String privateKeyId = System.getenv("GCP_PRIVATE_KEY_ID");

        BigQuery bq;

        if (clientEmail != null && !clientEmail.isEmpty() && privateKey != null && !privateKey.isEmpty()) {
            // 환경변수로 직접 인증
            String credJson = buildCredentialJson(gcpProjectId, clientEmail, privateKey, privateKeyId);
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(
                    new ByteArrayInputStream(credJson.getBytes(StandardCharsets.UTF_8))
            );
            bq = BigQueryOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId(gcpProjectId)
                    .build()
                    .getService();
            log.info("GCP 인증: 환경변수(GCP_PROJECT_ID, GCP_CLIENT_EMAIL, GCP_PRIVATE_KEY) 사용");
        } else if (credPath != null && !credPath.isEmpty()) {
            // 기존 파일 경로 방식
            bq = BigQueryOptions.getDefaultInstance().getService();
            log.info("GCP 인증: GOOGLE_APPLICATION_CREDENTIALS 파일 사용");
        } else {
            log.error("GCP 인증 정보가 없습니다. 다음 중 하나를 설정하세요:");
            log.error("  방법1(환경변수): GCP_PROJECT_ID, GCP_CLIENT_EMAIL, GCP_PRIVATE_KEY");
            log.error("  방법2(파일): GOOGLE_APPLICATION_CREDENTIALS=/path/to/key.json");
            throw new IllegalStateException("GCP 인증 환경변수를 설정하세요.");
        }

        this.projectId = bq.getOptions().getProjectId();
        log.info("BigQuery 연결 완료 - project: {}", projectId);

        // dataset/table 미지정 시 자동 탐색
        if (dataset == null || dataset.isEmpty() || table == null || table.isEmpty()) {
            autoDiscoverBillingTable(bq);
        }

        log.info("빌링 테이블: {}.{}.{}", projectId, dataset, table);
        return bq;
    }

    private String buildCredentialJson(String projectId, String clientEmail, String privateKey, String privateKeyId) {
        String escapedKey = privateKey.replace("\\n", "\n").replace("\n", "\\n");
        String keyIdField = (privateKeyId != null && !privateKeyId.isEmpty())
                ? String.format("\"private_key_id\": \"%s\",", privateKeyId)
                : "";
        return String.format("""
                {
                  "type": "service_account",
                  "project_id": "%s",
                  %s
                  "client_email": "%s",
                  "private_key": "%s",
                  "token_uri": "https://oauth2.googleapis.com/token"
                }
                """, projectId, keyIdField, clientEmail, escapedKey);
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
