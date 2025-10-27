package com.mcmp.cost.azure.collector.batch;

import lombok.Getter;

@Getter
public enum AzureBatchType {
    // 새로운 배치가 생기면 여기에 추가
    AZURE_COST_SERVICE(AzureBatchConstants.AZURE_COST_SERVICE_JOB, "Azure Cost Service"),
    AZURE_COST_VM(AzureBatchConstants.AZURE_COST_VM_JOB, "Azure Cost Vm"),
    ;

    private final String jobBeanName;
    private final String displayName;

    AzureBatchType(String jobBeanName, String displayName) {
        this.jobBeanName = jobBeanName;
        this.displayName = displayName;
    }
}
