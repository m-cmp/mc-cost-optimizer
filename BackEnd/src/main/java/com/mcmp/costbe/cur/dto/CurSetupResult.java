package com.mcmp.costbe.cur.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CurSetupResult {
    private final String bucketName;
    private final String reportName;
    private final List<StepResult> steps;
}
