package com.mcmp.cost.azure.collector.controller;

import com.mcmp.cost.azure.collector.batch.AsyncExecutorService;
import com.mcmp.cost.azure.collector.batch.AzureBatchType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class AzureBatchJobController {

    private final AsyncExecutorService batchExecutorService;

    @GetMapping(value = "/batch/azure/service")
    public ResponseEntity<String> batchAzureService() {
        batchExecutorService.asyncExecuteBatch(AzureBatchType.AZURE_COST_SERVICE);
        return ResponseEntity.ok("Azure Cost Service Batch Job started successfully");
    }

    @GetMapping(value = "/batch/azure/vm")
    public ResponseEntity<String> batchAzureVm() {
        batchExecutorService.asyncExecuteBatch(AzureBatchType.AZURE_COST_VM);
        return ResponseEntity.ok("Azure Cost VM Batch Job started successfully");
    }
}
