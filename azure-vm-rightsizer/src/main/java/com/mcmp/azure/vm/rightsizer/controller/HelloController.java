package com.mcmp.azure.vm.rightsizer.controller;

import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.service.RecommendVmService;
import com.mcmp.azure.vm.rightsizer.service.impl.AnomalyCostVmServiceImpl;
import com.mcmp.azure.vm.rightsizer.service.impl.RecommendVmServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class HelloController {

    private final RecommendVmServiceImpl recommendVmServiceImpl;
    private final RecommendVmService recommendVmService;
    private final AnomalyCostVmServiceImpl anomalyVmServiceImpl;

    @GetMapping(value = "/azure/recommend/test")
    public ResponseEntity<String> test1() {
        recommendVmServiceImpl.test();
        return ResponseEntity.ok("Test Recommend Up, Down, Modernize");
    }

    @GetMapping(value = "/azure/anomaly/test")
    public ResponseEntity<String> test2() {
        anomalyVmServiceImpl.test();
        return ResponseEntity.ok("Test Anomaly VM.");
    }

    @GetMapping(value = "/azure/recommend/test/up/{vmId}")
    public ResponseEntity<RecommendVmTypeDto> testUpVmId(@PathVariable String vmId) {
        // vm-capshp-prd-krc-web01
        RecommendVmTypeDto vmTypeDto = recommendVmService.getRecommendSizeUpVm(vmId);
        return ResponseEntity.ok(vmTypeDto);
    }

    @GetMapping(value = "/azure/recommend/test/down/{vmId}")
    public ResponseEntity<RecommendVmTypeDto> testDownVmId(@PathVariable String vmId) {
        // vm-capshp-prd-krc-web02
        RecommendVmTypeDto vmTypeDto = recommendVmService.getRecommendSizeDownVm(vmId);
        return ResponseEntity.ok(vmTypeDto);
    }


    @GetMapping(value = "/azure/recommend/test/modern/{vmId}")
    public ResponseEntity<RecommendVmTypeDto> testModernizeVmId(@PathVariable String vmId) {
        // vm-capshp-prd-krc-jumpbox
        RecommendVmTypeDto vmTypeDto = recommendVmService.getRecommendModernizeVm(vmId);
        return ResponseEntity.ok(vmTypeDto);
    }
}
