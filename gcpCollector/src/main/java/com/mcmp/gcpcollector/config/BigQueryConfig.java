package com.mcmp.gcpcollector.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.mcmp.gcpcollector.credential.CredentialResolver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Configuration
@Getter
public class BigQueryConfig {

    // env(@Value) 는 "원본 값". 실제 사용 값은 CredentialResolver 를 통해 (openbao.enabled 정책에 따라) 결정한다.
    @Value("${gcp.project-id:}")
    private String gcpProjectId;

    @Value("${gcp.client-email:}")
    private String clientEmail;

    @Value("${gcp.private-key:}")
    private String privateKey;

    @Value("${gcp.private-key-id:}")
    private String privateKeyId;

    @Value("${gcp.dataset:}")
    private String dataset;

    @Value("${gcp.table:}")
    private String table;

    private String projectId;

    private final CredentialResolver credentialResolver;

    public BigQueryConfig(CredentialResolver credentialResolver) {
        this.credentialResolver = credentialResolver;
    }

    @Bean
    public BigQuery bigQuery() throws Exception {
        String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        // 크레덴셜 결정: openbao.enabled=true → OpenBao, false → env 우선 후 OpenBao 폴백
        String resolvedProjectId  = credentialResolver.resolve("gcp", "project_id", gcpProjectId);
        String resolvedEmail      = credentialResolver.resolve("gcp", "client_email", clientEmail);
        String resolvedPrivateKey = credentialResolver.resolve("gcp", "private_key", privateKey);
        String resolvedKeyId      = credentialResolver.resolveOptional("gcp", "private_key_id", privateKeyId);

        BigQuery bq;

        if (resolvedEmail != null && !resolvedEmail.isEmpty()
                && resolvedPrivateKey != null && !resolvedPrivateKey.isEmpty()) {
            PrivateKey pk = parsePemPrivateKey(resolvedPrivateKey);
            ServiceAccountCredentials.Builder builder = ServiceAccountCredentials.newBuilder()
                    .setClientEmail(resolvedEmail)
                    .setPrivateKey(pk)
                    .setProjectId(resolvedProjectId);
            if (resolvedKeyId != null && !resolvedKeyId.isEmpty()) {
                builder.setPrivateKeyId(resolvedKeyId);
            }
            bq = BigQueryOptions.newBuilder()
                    .setCredentials(builder.build())
                    .setProjectId(resolvedProjectId)
                    .build()
                    .getService();
            log.info("GCP 인증: 서비스계정 크레덴셜 사용 (CredentialResolver 경유)");
        } else if (credPath != null && !credPath.isEmpty()) {
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

    private PrivateKey parsePemPrivateKey(String pem) throws Exception {
        String cleaned = pem.replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
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
