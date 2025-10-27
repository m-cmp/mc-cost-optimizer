package com.mcmp.cost.ncp.collector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServerInstanceDetailWrapper {

    /**
     * API 응답 Wrapper.
     */
    @JsonProperty("getServerInstanceDetailResponse")
    private ServerInstanceList getServerInstanceDetailResponse;
}
