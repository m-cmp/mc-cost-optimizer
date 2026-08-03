package com.mcmp.costbe.gcp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GcpDatasetRequest {
    /** "create" | "existing" */
    private String action;
    private String datasetName;
}
