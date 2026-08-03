package com.mcmp.costbe.gcp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GcpAdminSaRequest {
    /** Owner 권한 서비스 계정의 JSON 키 파일 전체 내용 */
    private String adminSaJson;
}
