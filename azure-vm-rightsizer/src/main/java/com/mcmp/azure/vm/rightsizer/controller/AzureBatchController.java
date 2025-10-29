package com.mcmp.azure.vm.rightsizer.controller;

import com.mcmp.azure.vm.rightsizer.batch.AsyncExecutorService;
import com.mcmp.azure.vm.rightsizer.batch.RightSizeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class AzureBatchController {

    private final AsyncExecutorService batchExecutorService;

    @GetMapping(value = "/batch/azure/recommend")
    public ResponseEntity<String> recommendAzureVm() {
        batchExecutorService.asyncExecuteBatch(RightSizeType.AZURE_SIZE_UP_VM);
        return ResponseEntity.ok("Azure Recommend VM Type Batch Job started successfully");
    }

    @GetMapping(value = "/batch/azure/anomaly")
    public ResponseEntity<String> anomalyAzureVm() {
        batchExecutorService.asyncExecuteBatch(RightSizeType.AZURE_ANOMALY_VM);
        return ResponseEntity.ok("Azure Anomaly VM Type Batch Job started successfully");
    }
}
