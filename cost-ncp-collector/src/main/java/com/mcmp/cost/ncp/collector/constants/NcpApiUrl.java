package com.mcmp.cost.ncp.collector.constants;

public class NcpApiUrl {

    private NcpApiUrl() {
        throw new IllegalStateException("Cannot instantiate a utility class.");
    }

    public static final String NCP_PUBLIC_BILLING_API_URL = "https://billingapi.apigw.ntruss.com/billing/v1";
    public static final String NCP_PUBLIC_SERVER_URL = "https://ncloud.apigw.ntruss.com/vserver/v2";

    public static final String PRODUCT_DEMAND_COST_LIST_URL = NCP_PUBLIC_BILLING_API_URL + "/cost/getProductDemandCostList";
    public static final String CONTRACT_DEMAND_COST_LIST_URL = NCP_PUBLIC_BILLING_API_URL + "/cost/getContractDemandCostList";
    public static final String SERVER_DETAIL_URL = NCP_PUBLIC_SERVER_URL + "/getServerInstanceDetail";

    /** NKS(Ncloud Kubernetes Service) 공개 API. 리전별 경로: KR=/vnks/v2, SGN=/vnks/sgn-v2, JPN=/vnks/jpn-v2 */
    public static final String NCP_PUBLIC_NKS_URL = "https://nks.apigw.ntruss.com";

    /** NKS 클러스터 목록 URL (uuid ↔ instanceNo 매칭용). */
    public static String nksClusterListUrl(String regionCode) {
        String segment = switch (regionCode == null ? "KR" : regionCode.toUpperCase()) {
            case "SGN" -> "/vnks/sgn-v2";
            case "JPN" -> "/vnks/jpn-v2";
            default -> "/vnks/v2";
        };
        return NCP_PUBLIC_NKS_URL + segment + "/clusters";
    }
}
