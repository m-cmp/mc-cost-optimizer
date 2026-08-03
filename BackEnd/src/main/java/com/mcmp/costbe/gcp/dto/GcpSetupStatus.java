package com.mcmp.costbe.gcp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GcpSetupStatus {

    public enum Stage {
        /** csp/gcp 크레덴셜 미등록 */
        NO_CREDENTIALS,
        /** 크레덴셜 있음, 데이터셋 미설정 */
        DATASET,
        /** 데이터셋 있음, 빌링 내보내기 미확인 */
        BILLING_GUIDE,
        /** 빌링 내보내기 확인됨, 테이블 생성 대기 */
        WAITING_TABLE,
        /** 테이블 감지 완료 — 수집 가능 */
        COMPLETE
    }

    private final Stage   stage;
    /** csp/gcp 에 어드민 SA 키 존재 여부 — NO_CREDENTIALS 일 때 자동 프로비저닝 가능 여부 판단용 */
    private final boolean adminKeyPresent;
    private final String  projectId;
    private final String  clientEmail;
    private final String  dataset;
    private final String  table;
}
