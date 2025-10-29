package com.mcmp.azure.vm.rightsizer.batch;

import lombok.Getter;

@Getter
public enum RightSizeType {
    // 새로운 배치가 생기면 여기에 추가
    AZURE_SIZE_UP_VM(RightSizeBatchConstants.SIZE_UP_JOB, "Azure Vm Size Up"),
    AZURE_SIZE_DOWN_VM(RightSizeBatchConstants.SIZE_DOWN_JOB, "Azure Vm Size Down"),
    AZURE_ANOMALY_VM(RightSizeBatchConstants.ANOMALY_JOB, "Azure Vm Anomaly"),
    ;

    private final String jobBeanName;
    private final String displayName;

    RightSizeType(String jobBeanName, String displayName) {
        this.jobBeanName = jobBeanName;
        this.displayName = displayName;
    }
}
