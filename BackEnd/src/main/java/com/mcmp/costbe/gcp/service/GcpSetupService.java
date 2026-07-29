package com.mcmp.costbe.gcp.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.mcmp.costbe.archive.credential.OpenBaoClient;
import com.mcmp.costbe.cur.dto.StepResult;
import com.mcmp.costbe.gcp.dto.GcpSetupResult;
import com.mcmp.costbe.gcp.dto.GcpSetupStatus;
import com.mcmp.costbe.gcp.exception.GcpSetupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpSetupService {

    private final OpenBaoClient openBaoClient;

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public GcpSetupResult setup() {
        synchronized (locks.computeIfAbsent("singleton", k -> new Object())) {
            return doSetup();
        }
    }

    public GcpSetupStatus getStatus() {
        Map<String, String> creds = openBaoClient.readPath("cost/gcp");
        boolean configured = creds.containsKey("project_id")
                && !creds.get("project_id").isBlank();
        return new GcpSetupStatus(configured);
    }

    private GcpSetupResult doSetup() {
        List<StepResult> steps = new ArrayList<>();
        log.info("[GcpSetup] ===== START =====");

        // Step 0: OpenBao write 권한 probe
        if (!runStep(steps, "OpenBao permission check", () -> {
            log.info("[GcpSetup] Probing OpenBao write permission at cost/gcp-probe");
            openBaoClient.writePath("cost/gcp-probe", Map.of("probe", "ok"));
            try {
                openBaoClient.deletePath("cost/gcp-probe");
                log.info("[GcpSetup] OpenBao write/delete OK");
            } catch (Exception e) {
                log.warn("[GcpSetup] Probe cleanup failed (continuing): {}", e.getMessage());
            }
        })) return new GcpSetupResult(null, null, null, steps);

        // Step 1: GCP 크레덴셜 확인 (csp/gcp)
        String[] gcpCreds = runStepGet(steps, "GCP credentials check", () -> {
            log.info("[GcpSetup] Reading GCP credentials from OpenBao secret/data/csp/gcp");
            Map<String, String> c = openBaoClient.readCsp("gcp");
            String projectId = c.get("project_id");
            String email = c.get("client_email");
            String key = c.get("private_key");
            if (projectId == null || projectId.isBlank())
                throw new GcpSetupException("project_id not found in OpenBao csp/gcp. Please register GCP SA credentials first.");
            if (email == null || email.isBlank())
                throw new GcpSetupException("client_email not found in OpenBao csp/gcp.");
            if (key == null || key.isBlank())
                throw new GcpSetupException("private_key not found in OpenBao csp/gcp.");
            log.info("[GcpSetup] GCP credentials found - project={}, email={}", projectId, email);
            return new String[]{projectId, email, key};
        });
        if (gcpCreds == null) return new GcpSetupResult(null, null, null, steps);

        String projectId = gcpCreds[0];
        String clientEmail = gcpCreds[1];
        String privateKey = gcpCreds[2];

        // Step 2: BigQuery 연결 테스트
        BigQuery bigQuery = runStepGet(steps, "BigQuery connection test", () -> {
            PrivateKey pk = parsePemPrivateKey(privateKey);
            ServiceAccountCredentials creds = ServiceAccountCredentials.newBuilder()
                    .setClientEmail(clientEmail)
                    .setPrivateKey(pk)
                    .setProjectId(projectId)
                    .build();
            BigQuery bq = BigQueryOptions.newBuilder()
                    .setCredentials(creds)
                    .setProjectId(projectId)
                    .build()
                    .getService();
            bq.listDatasets(projectId);
            log.info("[GcpSetup] BigQuery connection OK - project: {}", projectId);
            return bq;
        });
        if (bigQuery == null) return new GcpSetupResult(projectId, null, null, steps);

        // Step 3: 빌링 테이블 탐색 (cost/gcp에 이미 있으면 SKIP)
        Map<String, String> existingCostCreds = openBaoClient.readPath("cost/gcp");
        String existingDataset = existingCostCreds.get("dataset");
        String existingTable   = existingCostCreds.get("table");

        String dataset;
        String table;

        if (existingDataset != null && !existingDataset.isBlank()
                && existingTable != null && !existingTable.isBlank()) {
            log.info("[GcpSetup] Existing billing table found in cost/gcp, retaining: {}.{}", existingDataset, existingTable);
            steps.add(StepResult.skip("Billing table discovery",
                    "existing table retained → " + existingDataset + "." + existingTable));
            dataset = existingDataset;
            table   = existingTable;
        } else {
            String[] discovered = {null, null};
            try {
                log.info("[GcpSetup] Scanning all datasets in project: {}", projectId);
                outer:
                for (Dataset ds : bigQuery.listDatasets(projectId).iterateAll()) {
                    String dsName = ds.getDatasetId().getDataset();
                    for (Table tbl : bigQuery.listTables(dsName).iterateAll()) {
                        String tblName = tbl.getTableId().getTable();
                        if (tblName.startsWith("gcp_billing_export")) {
                            discovered[0] = dsName;
                            discovered[1] = tblName;
                            log.info("[GcpSetup] Billing table found: {}.{}", dsName, tblName);
                            break outer;
                        }
                    }
                }
            } catch (Exception e) {
                steps.add(StepResult.failed("Billing table discovery", e.getMessage()));
                log.error("[GcpSetup] Billing table discovery 오류: {}", e.getMessage(), e);
                return new GcpSetupResult(projectId, null, null, steps);
            }

            if (discovered[0] != null) {
                steps.add(StepResult.ok("Billing table discovery"));
                dataset = discovered[0];
                table   = discovered[1];
            } else {
                steps.add(StepResult.warn("Billing table discovery",
                        "Billing export table not found yet — GCP creates it within 24h after first export. " +
                        "Credentials will be saved; re-run Setup after table appears."));
                log.warn("[GcpSetup] Billing export table not found yet in project: {}", projectId);
                dataset = null;
                table   = null;
            }
        }

        // Step 4: cost/gcp 저장 (크레덴셜 + 탐색된 dataset/table)
        if (!runStep(steps, "Store to OpenBao (cost/gcp)", () -> {
            log.info("[GcpSetup] Writing to OpenBao secret/data/cost/gcp");
            Map<String, String> costData = new HashMap<>();
            costData.put("project_id",   projectId);
            costData.put("client_email", clientEmail);
            costData.put("private_key",  privateKey);
            if (dataset != null) costData.put("dataset", dataset);
            if (table   != null) costData.put("table",   table);
            openBaoClient.writePath("cost/gcp", costData);
            log.info("[GcpSetup] Stored: project={}, dataset={}, table={}", projectId, dataset, table);
        })) return new GcpSetupResult(projectId, dataset, table, steps);

        boolean allOk = steps.stream().noneMatch(s -> "FAILED".equals(s.getStatus()));
        log.info("[GcpSetup] ===== {} — project={}, dataset={}, table={} =====",
                allOk ? "COMPLETE" : "FAILED", projectId, dataset, table);
        return new GcpSetupResult(projectId, dataset, table, steps);
    }

    private static PrivateKey parsePemPrivateKey(String pem) throws Exception {
        String cleaned = pem.replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private boolean runStep(List<StepResult> steps, String name, ThrowingRunnable action) {
        try {
            action.run();
            steps.add(StepResult.ok(name));
            return true;
        } catch (GcpSetupException e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[GcpSetup] {} 실패: {}", name, e.getMessage());
            return false;
        } catch (Exception e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[GcpSetup] {} 오류: {}", name, e.getMessage(), e);
            return false;
        }
    }

    private <T> T runStepGet(List<StepResult> steps, String name, ThrowingSupplier<T> action) {
        try {
            T result = action.get();
            steps.add(StepResult.ok(name));
            return result;
        } catch (GcpSetupException e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[GcpSetup] {} 실패: {}", name, e.getMessage());
            return null;
        } catch (Exception e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[GcpSetup] {} 오류: {}", name, e.getMessage(), e);
            return null;
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
