package com.mcmp.cost.ncp.collector.batch;

import lombok.Getter;

@Getter
public enum NcpBatchType {
    // 새로운 배치가 생기면 여기에 추가
    NCP_COST_SERVICE(NcpBatchConstants.NCP_COST_SERVICE_JOB, "Ncp Cost Service"),
    NCP_COST_VM(NcpBatchConstants.NCP_COST_VM_JOB, "Ncp Cost Vm"),
    ;

    private final String jobBeanName;
    private final String displayName;

    NcpBatchType(String jobBeanName, String displayName) {
        this.jobBeanName = jobBeanName;
        this.displayName = displayName;
    }
}
