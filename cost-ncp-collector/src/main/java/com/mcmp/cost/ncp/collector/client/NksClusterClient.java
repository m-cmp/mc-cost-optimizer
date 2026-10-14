package com.mcmp.cost.ncp.collector.client;

import com.mcmp.cost.ncp.collector.constants.NcpApiUrl;
import com.mcmp.cost.ncp.collector.dto.NksClusterIndex;
import com.mcmp.cost.ncp.collector.dto.NksClusterListWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


/**
 * NKS 클러스터 목록 조회(GET /vnks/{region}/clusters). 과금 계약의 instanceNo 또는 instanceName ↔ 클러스터 uuid 매칭용.
 * 서명은 과금 API 와 같은 x-ncp-apigw-signature-v2 라 기존 RestClient(SigningInterceptor) 를 그대로 쓴다.
 * 실패 시 빈 인덱스를 돌려주고 수집은 계속된다(resource_id 는 instanceNo 로 폴백).
 */
@Slf4j
@Component
public class NksClusterClient {

    /** @return instanceNo·이름으로 찾을 수 있는 클러스터 인덱스. 조회 실패/빈 응답이면 빈 인덱스. */
    public NksClusterIndex list(RestClient restClient, String regionCode) {
        String url = NcpApiUrl.nksClusterListUrl(regionCode);
        try {
            NksClusterListWrapper wrapper = restClient.get().uri(url).retrieve().body(NksClusterListWrapper.class);
            if (wrapper == null || wrapper.getClusters() == null) {
                log.info("NKS 클러스터 목록이 비어 있습니다. region={}", regionCode);
                return NksClusterIndex.EMPTY;
            }
            NksClusterIndex index = NksClusterIndex.of(wrapper.getClusters());
            log.info("NKS 클러스터 {}개 조회 (region={}, instanceNo 매칭 {}개, 이름 매칭 {}개)",
                    wrapper.getClusters().size(), regionCode, index.byInstanceNo().size(), index.byName().size());
            return index;
        } catch (Exception e) {
            log.warn("NKS 클러스터 목록 조회 실패 ({}): {} — resource_id 는 instance_no 로 폴백합니다. (Sub Account 키에 NKS 조회 권한 필요)", url, e.getMessage());
            return NksClusterIndex.EMPTY;
        }
    }
}
