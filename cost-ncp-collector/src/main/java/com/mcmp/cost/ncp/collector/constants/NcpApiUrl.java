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
}
