package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 서버 인스턴스 목록
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-serverinstancelist">ServerInstanceList</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ServerInstanceList {

    private Integer totalRows;
    private List<ServerInstance> serverInstanceList;
}
