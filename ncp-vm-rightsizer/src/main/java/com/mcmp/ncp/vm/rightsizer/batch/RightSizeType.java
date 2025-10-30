package com.mcmp.ncp.vm.rightsizer.batch;

import lombok.Getter;

@Getter
public enum RightSizeType {
    // 새로운 배치가 생기면 여기에 추가
    NCP_SIZE_UP_VM(RightSizeBatchConstants.SIZE_UP_JOB, "NCP Vm Size Up"),
    NCP_SIZE_DOWN_VM(RightSizeBatchConstants.SIZE_DOWN_JOB, "NCP Vm Size Down"),
    NCP_ANOMALY_VM(RightSizeBatchConstants.ANOMALY_JOB, "NCP Vm Anomaly"),
    ;

    private final String jobBeanName;
    private final String displayName;

    RightSizeType(String jobBeanName, String displayName) {
        this.jobBeanName = jobBeanName;
        this.displayName = displayName;
    }
}
