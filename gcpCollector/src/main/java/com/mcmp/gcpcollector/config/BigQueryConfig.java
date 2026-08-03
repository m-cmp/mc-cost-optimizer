package com.mcmp.gcpcollector.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.mcmp.gcpcollector.credential.CredentialResolver;
import com.mcmp.gcpcollector.credential.OpenBaoClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Configuration
@Getter
public class BigQueryConfig {

    // 크레덴셜 env 폴백 (openbao.enabled=false 로컬 개발용)
    @Value("${gcp.project-id:}")
    private String gcpProjectId;

    @Value("${gcp.client-email:}")
    private String clientEmail;

    @Value("${gcp.private-key:}")
    private String privateKey;

    @Value("${gcp.private-key-id:}")
    private String privateKeyId;

    // dataset/table 은 Setup 완료 후 cost/gcp(OpenBao) 에서 읽어오므로 env 지정 불필요
    private String dataset;
    private String table;

    private String projectId;

    private final CredentialResolver credentialResolver;
    private final OpenBaoClient openBaoClient;

    public BigQueryConfig(CredentialResolver credentialResolver, OpenBaoClient openBaoClient) {
        this.credentialResolver = credentialResolver;
        this.openBaoClient = openBaoClient;
    }

    @Bean
    public BigQuery bigQuery() throws Exception {
        // 크레덴셜: cost/gcp(운영 SA) 우선 → 없으면 csp/gcp(어드민 SA) 폴백
        Map<String, String> costCreds = openBaoClient.readPath("cost/gcp");
        String opProjectId  = costCreds.get("project_id");
        String opEmail      = costCreds.get("client_email");
        String opPrivateKey = costCreds.get("private_key");

        String resolvedProjectId  = nonEmpty(opProjectId)  ? opProjectId  : credentialResolver.resolve("gcp", "project_id", gcpProjectId);
        String resolvedEmail      = nonEmpty(opEmail)       ? opEmail      : credentialResolver.resolve("gcp", "client_email", clientEmail);
        String resolvedPrivateKey = nonEmpty(opPrivateKey)  ? opPrivateKey : credentialResolver.resolve("gcp", "private_key", privateKey);
        String resolvedKeyId      = credentialResolver.resolveOptional("gcp", "private_key_id", privateKeyId);

        if (!nonEmpty(resolvedEmail) || !nonEmpty(resolvedPrivateKey)) {
            log.error("GCP 인증 정보 없음. cost/gcp 또는 csp/gcp 에 project_id/client_email/private_key 등록 필요.");
            throw new IllegalStateException("GCP 인증 정보가 없습니다.");
        }

        String credSource = nonEmpty(opEmail) ? "cost/gcp (운영 SA)" : "csp/gcp (어드민 SA)";
        log.info("GCP 크레덴셜 출처: {}", credSource);

        PrivateKey pk = parsePemPrivateKey(resolvedPrivateKey);
        ServiceAccountCredentials.Builder builder = ServiceAccountCredentials.newBuilder()
                .setClientEmail(resolvedEmail)
                .setPrivateKey(pk)
                .setProjectId(resolvedProjectId);
        if (nonEmpty(resolvedKeyId)) builder.setPrivateKeyId(resolvedKeyId);

        BigQuery bq = BigQueryOptions.newBuilder()
                .setCredentials(builder.build())
                .setProjectId(resolvedProjectId)
                .build()
                .getService();

        this.projectId = bq.getOptions().getProjectId();
        log.info("BigQuery 연결 완료 - project: {}", projectId);

        // dataset/table: cost/gcp 우선, 없으면 자동 탐색
        String costDataset = costCreds.get("dataset");
        String costTable   = costCreds.get("table");
        if (nonEmpty(costDataset) && nonEmpty(costTable)) {
            this.dataset = costDataset;
            this.table   = costTable;
            log.info("빌링 테이블 (OpenBao cost/gcp): {}.{}", this.dataset, this.table);
        } else {
            autoDiscoverBillingTable(bq);
        }

        log.info("빌링 테이블: {}.{}.{}", projectId, dataset, table);
        return bq;
    }

    private boolean nonEmpty(String s) { return s != null && !s.isEmpty(); }

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
            for (Dataset ds : bq.listDatasets().iterateAll()) {
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

        log.warn("빌링 내보내기 테이블을 찾지 못했습니다. GCP Setup을 완료하거나 GCP Console에서 Billing Export를 활성화하세요.");
    }
}
