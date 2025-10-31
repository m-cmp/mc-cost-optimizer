package com.mcmp.ncp.vm.rightsizer.controller;

import com.mcmp.ncp.vm.rightsizer.batch.AsyncExecutorService;
import com.mcmp.ncp.vm.rightsizer.batch.RightSizeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class NcpBatchController {

    private final AsyncExecutorService batchExecutorService;

    @GetMapping(value = "/batch/ncp/recommend")
    public ResponseEntity<String> recommendNcpVm() {
        batchExecutorService.asyncExecuteBatch(RightSizeType.NCP_SIZE_DOWN_VM);
        return ResponseEntity.ok("NCP Recommend VM Type Batch Job started successfully");
    }

    @GetMapping(value = "/batch/ncp/anomaly")
    public ResponseEntity<String> anomalyNcpVm() {
        batchExecutorService.asyncExecuteBatch(RightSizeType.NCP_ANOMALY_VM);
        return ResponseEntity.ok("NCP Anomaly VM Type Batch Job started successfully");
    }
}
