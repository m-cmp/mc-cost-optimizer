package com.mcmp.costbe.archive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** CSV(gzip) 직렬화 결과. byteSize = gzip 바이트 수(= S3 저장·HEAD 검증 대상). */
@Getter
@AllArgsConstructor
public class SerializedCsv {
    private final byte[] gz;
    private final long   rowCount;
    private final String sha256;
    private final long   byteSize;
}
