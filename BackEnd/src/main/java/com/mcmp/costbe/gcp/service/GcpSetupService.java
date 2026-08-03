package com.mcmp.costbe.gcp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.google.cloud.resourcemanager.v3.ProjectsClient;
import com.google.cloud.resourcemanager.v3.ProjectsSettings;
import com.google.iam.v1.Binding;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;
import com.mcmp.costbe.archive.credential.OpenBaoClient;
import com.mcmp.costbe.cur.dto.StepResult;
import com.mcmp.costbe.gcp.dao.GcpSetupDao;
import com.mcmp.costbe.gcp.dto.GcpSetupResult;
import com.mcmp.costbe.gcp.dto.GcpSetupStatus;
import com.mcmp.costbe.gcp.dto.GcpSetupStatus.Stage;
import com.mcmp.costbe.gcp.exception.GcpSetupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpSetupService {

    private final OpenBaoClient openBaoClient;
    private final GcpSetupDao   gcpSetupDao;

    private static final ObjectMapper MAPPER           = new ObjectMapper();
    private static final RestTemplate IAM_REST         = new RestTemplate();
    private static final String OP_SA_ACCOUNT_ID       = "mcmp-cost-collector";
    private static final String OP_DATASET_DEFAULT     = "mcmp_billing_export";
    private static final String DATASET_LOCATION       = "asia-northeast3";

    // ──────────────────────────────────────────────────────────────────────────
    // Auto-Provision: SA 생성 + 역할 부여 + 데이터셋 생성 (csp/gcp 어드민 키 사용)
    // 결과는 cost/gcp 에 저장 (기존 dataset/confirmed/table 값 보존)
    // ──────────────────────────────────────────────────────────────────────────

    public GcpSetupResult autoProvision() {
        List<StepResult> steps = new ArrayList<>();
        log.info("[GcpSetup] ===== AUTO PROVISION START =====");

        // Step 1: csp/gcp 어드민 SA 읽기 + 액세스 토큰 발급
        String[] adminCtx = runStepGet(steps, "Admin SA authentication", () -> {
            Map<String, String> csp = openBaoClient.readCsp("gcp");
            String projectId  = csp.get("project_id");
            String email      = csp.get("client_email");
            String privateKey = csp.get("private_key");
            if (blank(projectId))  throw new GcpSetupException("csp/gcp 에 project_id 없음");
            if (blank(email))      throw new GcpSetupException("csp/gcp 에 client_email 없음");
            if (blank(privateKey)) throw new GcpSetupException("csp/gcp 에 private_key 없음");

            PrivateKey pk = parsePemPrivateKey(privateKey);
            ServiceAccountCredentials adminCreds = ServiceAccountCredentials.newBuilder()
                    .setClientEmail(email).setPrivateKey(pk).setProjectId(projectId)
                    .setScopes(List.of("https://www.googleapis.com/auth/cloud-platform")).build();
            adminCreds.refreshIfExpired();
            return new String[]{ projectId, email, privateKey, adminCreds.getAccessToken().getTokenValue() };
        });
        if (adminCtx == null) return new GcpSetupResult(null, null, null, steps);

        String projectId     = adminCtx[0];
        String adminEmail    = adminCtx[1];
        String adminKey      = adminCtx[2];
        String adminToken    = adminCtx[3];

        // Step 2: 운영 SA 생성 (이미 존재하면 재사용)
        String opSaEmail = runStepGet(steps, "Operator SA creation", () -> {
            String opEmail  = OP_SA_ACCOUNT_ID + "@" + projectId + ".iam.gserviceaccount.com";
            String getUrl   = "https://iam.googleapis.com/v1/projects/" + projectId + "/serviceAccounts/" + opEmail;
            try {
                JsonNode existing = iamGet(adminToken, getUrl);
                log.info("[GcpSetup] 운영 SA 이미 존재: {}", existing.path("email").asText());
                return existing.path("email").asText();
            } catch (HttpClientErrorException.NotFound ignored) {}

            String createUrl = "https://iam.googleapis.com/v1/projects/" + projectId + "/serviceAccounts";
            JsonNode created = iamPost(adminToken, createUrl, Map.of(
                    "accountId", OP_SA_ACCOUNT_ID,
                    "serviceAccount", Map.of("displayName", "MCMP Cost Collector")));
            String email = created.path("email").asText();
            log.info("[GcpSetup] 운영 SA 생성 완료: {}", email);
            return email;
        });
        if (opSaEmail == null) return new GcpSetupResult(projectId, null, null, steps);

        // Step 3: 운영 SA에 최소 역할 부여 (어드민 SA 사용)
        final String fProjectId = projectId;
        final String fOpEmail   = opSaEmail;
        if (!runStep(steps, "SA role binding", () -> {
            PrivateKey pk = parsePemPrivateKey(adminKey);
            ServiceAccountCredentials adminCreds = ServiceAccountCredentials.newBuilder()
                    .setClientEmail(adminEmail).setPrivateKey(pk).setProjectId(fProjectId)
                    .setScopes(List.of("https://www.googleapis.com/auth/cloud-platform")).build();
            grantRolesToTarget(fProjectId, adminCreds, fOpEmail);
        })) return new GcpSetupResult(projectId, null, null, steps);

        // Step 4: 운영 SA 키 생성 (IAM 전파 지연 대비 최대 5회 재시도)
        String opKeyJson = runStepGet(steps, "SA key generation", () -> {
            String keyUrl = "https://iam.googleapis.com/v1/projects/" + fProjectId
                    + "/serviceAccounts/" + fOpEmail + "/keys";
            HttpClientErrorException.NotFound lastErr = null;
            for (int attempt = 1; attempt <= 5; attempt++) {
                try {
                    JsonNode keyResp = iamPost(adminToken, keyUrl, Map.of());
                    byte[] decoded = Base64.getMimeDecoder().decode(keyResp.path("privateKeyData").asText());
                    return new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
                } catch (HttpClientErrorException.NotFound e) {
                    lastErr = e;
                    log.warn("[GcpSetup] SA 키 생성 재시도 {}/5 (IAM 전파 지연)", attempt);
                    Thread.sleep(3000L * attempt);
                }
            }
            throw lastErr;
        });
        if (opKeyJson == null) return new GcpSetupResult(projectId, null, null, steps);

        // Step 5: cost/gcp 에 운영 SA 키 저장 (기존 dataset/confirmed/table 보존)
        if (!runStep(steps, "Save to OpenBao (cost/gcp)", () -> {
            JsonNode keyNode = MAPPER.readTree(opKeyJson);
            Map<String, String> cost = new HashMap<>(safeRead(() -> openBaoClient.readPath("cost/gcp")));
            cost.put("project_id",   keyNode.path("project_id").asText());
            cost.put("client_email", keyNode.path("client_email").asText());
            cost.put("private_key",  keyNode.path("private_key").asText());
            openBaoClient.writePath("cost/gcp", cost);
            log.info("[GcpSetup] 운영 SA 저장 완료: {}", keyNode.path("client_email").asText());
        })) return new GcpSetupResult(projectId, null, null, steps);

        log.info("[GcpSetup] ===== AUTO PROVISION COMPLETE =====");
        return new GcpSetupResult(projectId, null, null, steps);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Status
    // ──────────────────────────────────────────────────────────────────────────

    public GcpSetupStatus getStatus() {
        Map<String, String> csp  = safeRead(() -> openBaoClient.readCsp("gcp"));
        Map<String, String> cost = safeRead(() -> openBaoClient.readPath("cost/gcp"));

        // 어드민 SA 존재 여부 (자동 프로비저닝 가능 여부)
        boolean adminKeyPresent = !blank(csp.get("project_id")) && !blank(csp.get("client_email"));

        // 수집용 크레덴셜은 cost/gcp (운영 SA)
        String projectId = cost.getOrDefault("project_id", "");
        String email     = cost.getOrDefault("client_email", "");
        String dataset   = cost.getOrDefault("dataset", "");
        String confirmed = cost.getOrDefault("billing_export_confirmed", "");
        String table     = cost.getOrDefault("table", "");

        Stage stage;
        if (blank(projectId) || blank(email)) stage = Stage.NO_CREDENTIALS;
        else if (blank(dataset))              stage = Stage.DATASET;
        else if (blank(confirmed))            stage = Stage.BILLING_GUIDE;
        else if (blank(table))                stage = Stage.WAITING_TABLE;
        else                                  stage = Stage.COMPLETE;

        return new GcpSetupStatus(
                stage,
                adminKeyPresent,
                blank(projectId) ? null : projectId,
                blank(email)     ? null : email,
                blank(dataset)   ? null : dataset,
                blank(table)     ? null : table
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Stage 1: 데이터셋 이름 변경 (사용자가 직접 변경할 경우에만 호출)
    // ──────────────────────────────────────────────────────────────────────────

    public void saveDataset(String action, String datasetName) {
        if (blank(datasetName)) throw new GcpSetupException("데이터셋 이름이 비어있습니다.");
        if (!"create".equalsIgnoreCase(action) && !"existing".equalsIgnoreCase(action))
            throw new GcpSetupException("action은 'create' 또는 'existing' 이어야 합니다.");

        // 크레덴셜은 cost/gcp (운영 SA)
        Map<String, String> cost = new HashMap<>(safeRead(() -> openBaoClient.readPath("cost/gcp")));
        String projectId   = cost.get("project_id");
        String clientEmail = cost.get("client_email");
        String privateKey  = cost.get("private_key");
        if (blank(projectId))
            throw new GcpSetupException("운영 SA 크레덴셜이 없습니다. 자동 설정을 먼저 실행해주세요.");

        if ("create".equalsIgnoreCase(action)) {
            if (blank(clientEmail) || blank(privateKey))
                throw new GcpSetupException("운영 SA 크레덴셜이 불완전합니다.");
            try {
                BigQuery bq = buildBigQuery(projectId, clientEmail, privateKey);
                DatasetId dsId = DatasetId.of(projectId, datasetName.trim());
                if (bq.getDataset(dsId) == null) {
                    bq.create(DatasetInfo.newBuilder(dsId).setLocation(DATASET_LOCATION).build());
                    log.info("[GcpSetup] BigQuery 데이터셋 생성 완료: {}", datasetName);
                } else {
                    log.info("[GcpSetup] BigQuery 데이터셋 이미 존재, 재사용: {}", datasetName);
                }
            } catch (GcpSetupException e) {
                throw e;
            } catch (Exception e) {
                throw new GcpSetupException("BigQuery 데이터셋 생성 실패: " + e.getMessage());
            }
        }

        boolean changed = !datasetName.trim().equals(cost.get("dataset"));
        cost.put("dataset",        datasetName.trim());
        cost.put("dataset_action", action.toLowerCase());
        if (changed) {
            cost.remove("billing_export_confirmed");
            cost.remove("table");
            log.info("[GcpSetup] 데이터셋 변경 → confirmed/table 초기화: {}", datasetName);
        }
        openBaoClient.writePath("cost/gcp", cost);
        log.info("[GcpSetup] 데이터셋 저장 완료: action={}, dataset={}", action, datasetName);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Stage 2: 빌링 내보내기 확인
    // ──────────────────────────────────────────────────────────────────────────

    public GcpSetupResult confirmBillingExport() {
        List<StepResult> steps = new ArrayList<>();
        log.info("[GcpSetup] ===== BILLING EXPORT CONFIRM START =====");

        // Step 1: cost/gcp 운영 SA 크레덴셜 읽기
        String[] creds = runStepGet(steps, "GCP credentials check", () -> {
            Map<String, String> c = openBaoClient.readPath("cost/gcp");
            String projectId = c.get("project_id");
            String email     = c.get("client_email");
            String key       = c.get("private_key");
            if (blank(projectId)) throw new GcpSetupException("project_id 미등록 (자동 설정 먼저 실행)");
            if (blank(email))     throw new GcpSetupException("client_email 미등록");
            if (blank(key))       throw new GcpSetupException("private_key 미등록");
            return new String[]{projectId, email, key};
        });
        if (creds == null) return new GcpSetupResult(null, null, null, steps);

        String projectId   = creds[0];
        String clientEmail = creds[1];
        String privateKey  = creds[2];

        // Step 2: 데이터셋 확인
        Map<String, String> cost = safeRead(() -> openBaoClient.readPath("cost/gcp"));
        String dataset = cost.get("dataset");
        if (blank(dataset)) {
            steps.add(StepResult.failed("Dataset check", "데이터셋 이름이 설정되지 않았습니다."));
            return new GcpSetupResult(projectId, null, null, steps);
        }
        steps.add(StepResult.ok("Dataset check", dataset));

        // Step 3: BigQuery 연결 테스트
        BigQuery bq = runStepGet(steps, "BigQuery connection test", () -> {
            BigQuery client = buildBigQuery(projectId, clientEmail, privateKey);
            client.listDatasets(BigQuery.DatasetListOption.pageSize(1));
            return client;
        });
        if (bq == null) return new GcpSetupResult(projectId, dataset, null, steps);

        // Step 4: 빌링 테이블 탐색
        String foundTable = null;
        try {
            foundTable = scanDatasetForBillingTable(bq, dataset);
            if (foundTable != null) {
                steps.add(StepResult.ok("Billing table scan", dataset + "." + foundTable + " 발견"));
            } else {
                steps.add(StepResult.ok("Billing table scan", "아직 미생성 — 매일 09:00 자동 감지"));
            }
        } catch (Exception e) {
            steps.add(StepResult.warn("Billing table scan", e.getMessage()));
        }

        // Step 5: OpenBao 저장
        final String finalDataset = dataset;
        final String finalTable   = foundTable;
        if (!runStep(steps, "Save to OpenBao (cost/gcp)", () -> {
            Map<String, String> updated = new HashMap<>(cost);
            updated.put("billing_export_confirmed", "true");
            if (finalTable != null) updated.put("table", finalTable);
            openBaoClient.writePath("cost/gcp", updated);
        })) return new GcpSetupResult(projectId, finalDataset, null, steps);

        // Step 6: DB 등록
        runStep(steps, "DB registration", () -> gcpSetupDao.upsertGcpSetup(finalDataset, finalTable));

        log.info("[GcpSetup] BILLING CONFIRM COMPLETE — dataset={}, table={}", finalDataset, foundTable);
        return new GcpSetupResult(projectId, finalDataset, foundTable, steps);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Stage 3: 빌링 테이블 자동 감지 (매일 09:00)
    // ──────────────────────────────────────────────────────────────────────────

    @Scheduled(cron = "0 0 9 * * ?")
    public void pollBillingTable() {
        Map<String, String> cost = safeRead(() -> openBaoClient.readPath("cost/gcp"));
        String dataset   = cost.get("dataset");
        String table     = cost.get("table");
        String confirmed = cost.get("billing_export_confirmed");
        if (blank(dataset) || !blank(table) || blank(confirmed)) return;

        log.info("[GcpSetup][Poll] 빌링 테이블 탐색 — dataset={}", dataset);
        try {
            String projectId  = cost.get("project_id");
            String email      = cost.get("client_email");
            String privateKey = cost.get("private_key");
            if (blank(projectId) || blank(email) || blank(privateKey)) return;

            BigQuery bq   = buildBigQuery(projectId, email, privateKey);
            String  found = scanDatasetForBillingTable(bq, dataset);
            if (found != null) {
                log.info("[GcpSetup][Poll] 테이블 발견: {}.{}", dataset, found);
                Map<String, String> updated = new HashMap<>(cost);
                updated.put("table", found);
                openBaoClient.writePath("cost/gcp", updated);
                gcpSetupDao.upsertGcpSetup(dataset, found);
            } else {
                log.info("[GcpSetup][Poll] 테이블 미발견, 내일 재시도");
            }
        } catch (Exception e) {
            log.warn("[GcpSetup][Poll] 탐색 오류: {}", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private BigQuery buildBigQuery(String projectId, String email, String privateKey) throws Exception {
        PrivateKey pk = parsePemPrivateKey(privateKey);
        return BigQueryOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.newBuilder()
                        .setClientEmail(email).setPrivateKey(pk).setProjectId(projectId).build())
                .setProjectId(projectId).build().getService();
    }

    private String scanDatasetForBillingTable(BigQuery bq, String dataset) {
        for (Table t : bq.listTables(dataset).iterateAll()) {
            if (t.getTableId().getTable().startsWith("gcp_billing_export")) return t.getTableId().getTable();
        }
        return null;
    }

    private static final List<String> SA_ROLES = List.of(
            "roles/iam.serviceAccountUser",
            "roles/bigquery.dataEditor",
            "roles/bigquery.jobUser",
            "roles/compute.admin"
    );

    /** 어드민 크레덴셜로 targetEmail SA에 SA_ROLES 부여. */
    private void grantRolesToTarget(String projectId, ServiceAccountCredentials adminCreds, String targetEmail) throws Exception {
        try (ProjectsClient client = ProjectsClient.create(ProjectsSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(adminCreds)).build())) {
            String resource = "projects/" + projectId;
            String member   = "serviceAccount:" + targetEmail;
            Policy policy   = client.getIamPolicy(GetIamPolicyRequest.newBuilder().setResource(resource).build());

            Map<String, Set<String>> existing = new HashMap<>();
            for (Binding b : policy.getBindingsList())
                existing.put(b.getRole(), new HashSet<>(b.getMembersList()));

            Policy.Builder builder = policy.toBuilder();
            for (String role : SA_ROLES) {
                if (existing.getOrDefault(role, Set.of()).contains(member)) continue;
                builder.addBindings(Binding.newBuilder().setRole(role).addMembers(member).build());
                log.info("[GcpSetup] IAM 추가: {} → {}", member, role);
            }
            client.setIamPolicy(SetIamPolicyRequest.newBuilder()
                    .setResource(resource).setPolicy(builder.build()).build());
            log.info("[GcpSetup] IAM 역할 부여 완료: {}", targetEmail);
        }
    }

    private JsonNode iamGet(String token, String url) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        ResponseEntity<JsonNode> res = IAM_REST.exchange(url, HttpMethod.GET, new HttpEntity<>(h), JsonNode.class);
        return res.getBody();
    }

    private JsonNode iamPost(String token, String url, Object body) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<JsonNode> res = IAM_REST.exchange(url, HttpMethod.POST, new HttpEntity<>(body, h), JsonNode.class);
        return res.getBody();
    }

    private static PrivateKey parsePemPrivateKey(String pem) throws Exception {
        String cleaned = pem.replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        return KeyFactory.getInstance("RSA").generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(cleaned)));
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

    private Map<String, String> safeRead(ThrowingSupplier<Map<String, String>> reader) {
        try { return reader.get(); } catch (Exception e) { return new HashMap<>(); }
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }

    @FunctionalInterface private interface ThrowingRunnable    { void run() throws Exception; }
    @FunctionalInterface private interface ThrowingSupplier<T> { T get()  throws Exception; }
}
