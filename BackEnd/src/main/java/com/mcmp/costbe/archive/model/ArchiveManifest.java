package com.mcmp.costbe.archive.model;

import lombok.Data;

import java.time.LocalDateTime;

/** cost_archive_manifest 행. 아카이브 운영 기록의 단일 진실원. */
@Data
public class ArchiveManifest {
    private String yearMonth;
    private String csp;
    private String s3Key;
    private Long   rowCount;
    private Long   byteSize;
    private String sha256;
    private String status;          // ARCHIVED / VERIFIED / FAILED
    private Boolean rawPurged;
    private LocalDateTime rawPurgedAt;
    private LocalDateTime archivedAt;
    private LocalDateTime updatedAt;
}
