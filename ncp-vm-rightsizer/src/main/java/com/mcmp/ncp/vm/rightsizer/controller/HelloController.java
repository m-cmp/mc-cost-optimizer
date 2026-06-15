package com.mcmp.ncp.vm.rightsizer.controller;

import com.mcmp.ncp.vm.rightsizer.service.impl.AnomalyCostVmServiceImpl;
import com.mcmp.ncp.vm.rightsizer.service.impl.RecommendVmServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class HelloController {

    private final RecommendVmServiceImpl recommendVmServiceImpl;
    private final AnomalyCostVmServiceImpl anomalyCostVmServiceImpl;

    @GetMapping(value = "/ncp/recommend/test")
    public ResponseEntity<String> test() {
        recommendVmServiceImpl.test();
        return ResponseEntity.ok("Test Recommend Up, Down");
    }

    @GetMapping(value = "/ncp/anomaly/test")
    public ResponseEntity<String> test2() {
        anomalyCostVmServiceImpl.test();
        return ResponseEntity.ok("Test Anomaly VM.");
    }

}
