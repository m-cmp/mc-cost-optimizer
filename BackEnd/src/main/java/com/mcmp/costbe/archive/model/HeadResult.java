package com.mcmp.costbe.archive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** S3 HEAD 검증 결과. */
@Data
@AllArgsConstructor
public class HeadResult {
    private String  bucket;
    private String  key;
    private long    byteSize;
    private boolean exists;
}
