package com.mcmp.costbe.archive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** S3 업로드 결과. */
@Data
@AllArgsConstructor
public class PutResult {
    private String bucket;
    private String key;       // prefix 포함 전체 키
    private long   byteSize;
}
