package com.mcmp.cost.ncp.collector.controller;

import com.mcmp.cost.ncp.collector.batch.AsyncExecutorService;
import com.mcmp.cost.ncp.collector.batch.NcpBatchType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class NcpBatchJobController {

    private final AsyncExecutorService asyncExecutorService;

    @GetMapping(value = "/batch/ncp/service")
    public ResponseEntity<String> batchNcpService() {
        asyncExecutorService.asyncExecuteBatch(NcpBatchType.NCP_COST_SERVICE);
        return ResponseEntity.ok("Ncp Cost Service Batch Job started successfully");
    }

    @GetMapping(value = "/batch/ncp/vm")
    public ResponseEntity<String> batchNcpVm() {
        asyncExecutorService.asyncExecuteBatch(NcpBatchType.NCP_COST_VM);
        return ResponseEntity.ok("Ncp Cost Vm Batch Job started successfully");
    }
}
