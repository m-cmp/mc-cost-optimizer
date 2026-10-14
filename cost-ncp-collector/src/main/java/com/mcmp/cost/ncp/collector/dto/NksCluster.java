package com.mcmp.cost.ncp.collector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** NKS 클러스터 목록(GET /vnks/v2/clusters) 항목 중 매칭에 필요한 필드. uuid 가 Tumblebug cspResourceId 와 일치한다. */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NksCluster {
    private String uuid;
    private Long id;
    private String name;
    private Long instanceNo;
    private String regionCode;
    private String status;
}
