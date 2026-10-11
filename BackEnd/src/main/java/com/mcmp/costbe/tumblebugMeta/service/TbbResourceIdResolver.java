package com.mcmp.costbe.tumblebugMeta.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tumblebug 자원 정보를 각 CSP 과금 데이터의 자원 식별자(servicegroup_meta.csp_instanceid)로 정규화하는 순수 함수 모음.
 *
 * <p>근거(cb-spider IId.SystemId → Tumblebug cspResourceId):
 * AWS EKS = 클러스터 이름(CUR은 ARN) / Azure AKS = ARM 전체 ID / GCP GKE = 클러스터 이름 / NCP NKS = UUID.
 * Object Storage는 전 CSP 공통으로 CSP측 버킷명.
 */
public final class TbbResourceIdResolver {

    private static final Pattern AWS_EKS_ARN_ACCOUNT = Pattern.compile("^arn:aws:eks:[^:]*:(\\d{12}):");
    private static final Pattern AZURE_SUBSCRIPTION = Pattern.compile("/subscriptions/([a-f0-9-]{36})", Pattern.CASE_INSENSITIVE);
    private static final Pattern GCP_PROJECT_IN_LINK = Pattern.compile("/projects/([\\w-]+)/");

    private TbbResourceIdResolver() {}

    /**
     * K8s 클러스터의 과금 매칭 키.
     * AWS만 keyValueList의 "Arn"으로 치환(CUR lineitem_resourceid가 ARN), 그 외 CSP는 cspResourceId 그대로.
     */
    public static String k8sInstanceId(String cspType, String cspResourceId, List<Map<String, String>> keyValueList) {
        if ("AWS".equalsIgnoreCase(cspType)) {
            String arn = kvValue(keyValueList, "Arn");
            if (arn != null && arn.startsWith("arn:aws:eks:")) {
                return arn;
            }
        }
        return cspResourceId;
    }

    private static final Pattern AWS_EC2_INSTANCE_ID = Pattern.compile("^i-[0-9a-f]{8,17}$");

    /**
     * K8s 워커 노드의 과금 매칭 키. 지금은 AWS만 지원: EKS 노드는 CUR에 AmazonEC2 인스턴스 ID(i-…)로 청구된다.
     * 다른 CSP는 노드 청구 단위가 달라(Azure = VM 스케일셋, NCP = 서버 VM 비용 테이블) 매칭 기준이 정해지기 전까지 null.
     */
    public static String k8sNodeInstanceId(String cspType, String nodeCspResourceId) {
        if ("AWS".equalsIgnoreCase(cspType) && nodeCspResourceId != null
                && AWS_EC2_INSTANCE_ID.matcher(nodeCspResourceId).matches()) {
            return nodeCspResourceId;
        }
        return null;
    }

    /** AWS EKS 클러스터 ARN 형식인지 (CUR lineitem_resourceid와 매칭 가능한 형태). */
    public static boolean isEksArn(String id) {
        return id != null && id.startsWith("arn:aws:eks:");
    }

    /**
     * Tumblebug 응답에 Arn이 없을 때 CUR에서 같은 클러스터의 ARN을 찾기 위한 LIKE 패턴.
     * arn:aws:eks:{region}:{account}:cluster/{name} — region을 모르면 와일드카드. name의 LIKE 특수문자(%, _, \)는 이스케이프.
     */
    public static String eksArnLikePattern(String region, String clusterName) {
        if (isBlank(clusterName)) return null;
        String r = isBlank(region) ? "%" : escapeLike(region);
        return "arn:aws:eks:" + r + ":%:cluster/" + escapeLike(clusterName);
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * K8s 클러스터의 CSP 계정 식별자.
     * AWS = ARN의 account id, AZURE = subscription id, GCP = SelfLink의 project id, 그 외/실패 = connectionName.
     */
    public static String k8sAccount(String cspType, String instanceId, List<Map<String, String>> keyValueList, String connectionName) {
        String fallback = isBlank(connectionName) ? "mcmpcostopti" : connectionName;
        if (cspType == null) {
            return fallback;
        }
        switch (cspType.toUpperCase()) {
            case "AWS": {
                Matcher m = AWS_EKS_ARN_ACCOUNT.matcher(nz(instanceId));
                if (m.find()) return m.group(1);
                break;
            }
            case "AZURE": {
                Matcher m = AZURE_SUBSCRIPTION.matcher(nz(instanceId));
                if (m.find()) return m.group(1);
                break;
            }
            case "GCP": {
                Matcher m = GCP_PROJECT_IN_LINK.matcher(nz(kvValue(keyValueList, "SelfLink")));
                if (m.find()) return m.group(1);
                break;
            }
            default:
                break;
        }
        return fallback;
    }

    /**
     * Object Storage의 과금 매칭 키 = CSP측 버킷/컨테이너 이름.
     * Tumblebug은 사용자가 준 name이 아니라 자기 uid를 CSP 버킷명으로 생성하고(cb-tumblebug resource/object_storage.go),
     * 생성 완료 후 cspResourceId = cb-spider SystemId = uid 를 채운다. 따라서
     * cspResourceId → uid → cspResourceName(= NameId = uid) 순으로 폴백하고, 사용자 name은 CSP 식별자가 아니므로 쓰지 않는다.
     * (Creating/Failed 상태에서는 cspResourceId가 비어 있을 수 있는데, 그때 name을 쓰면 나중에 uid 행과 중복되는 죽은 행이 생긴다)
     */
    public static String objectStorageInstanceId(String cspResourceId, String uid, String cspResourceName) {
        if (!isBlank(cspResourceId)) return cspResourceId;
        if (!isBlank(uid)) return uid;
        return isBlank(cspResourceName) ? null : cspResourceName;
    }

    /**
     * connectionConfig.providerName이 비어 있을 때의 CSP 판별 폴백.
     * Tumblebug 기본 연결 이름은 "{provider}-{region}" (예: aws-ap-northeast-2, azure-koreacentral, gcp-asia-northeast3, ncp-kr).
     * 아는 provider 접두사면 대문자 CSP 코드를, 아니면 null 을 돌려준다.
     */
    public static String cspTypeFromConnectionName(String connectionName) {
        if (isBlank(connectionName)) return null;
        String prefix = connectionName.split("-", 2)[0].trim().toUpperCase();
        switch (prefix) {
            case "AWS": case "AZURE": case "GCP": case "NCP":
                return prefix;
            default:
                return null;
        }
    }

    /** keyValueList([{key, value}])에서 key(대소문자 무시)에 해당하는 value. 없으면 null. */
    static String kvValue(List<Map<String, String>> keyValueList, String key) {
        if (keyValueList == null || key == null) return null;
        return keyValueList.stream()
                .filter(Objects::nonNull)
                .filter(m -> key.equalsIgnoreCase(m.get("key")))
                .map(m -> m.get("value"))
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
