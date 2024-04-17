package com.mcmp.dummybe.controller;

import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.service.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping(path = "/api/v3/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService service;

    @PostMapping(path = "/widget/dashboards")
    public ResponseEntity<ResultModel> getdashboards() throws IOException {
        ResultModel result;
        result = service.dashboardsDefine();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
//    @PostMapping(path = "/widget/dashboards")
//    public ResponseEntity<String> getdashboards() throws IOException {
//        ClassPathResource dashboardsResource = new ClassPathResource("dummys/dashboard/dashboardsdummy.json");
//        String dashboardsContent = new String(Files.readAllBytes(Paths.get(dashboardsResource.getURI())), StandardCharsets.UTF_8);
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(dashboardsContent);
//    }

    @PostMapping(path = "/save_as")
    public ResponseEntity<String> getsave_as() throws IOException {
        ClassPathResource saveasResource = new ClassPathResource("dummys/dashboard/saveasdummy.json");

        String saveasContent = new String(Files.readAllBytes(Paths.get(saveasResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(saveasContent);
    }
}
