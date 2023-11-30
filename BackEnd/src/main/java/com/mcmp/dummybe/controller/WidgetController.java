package com.mcmp.dummybe.controller;

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
public class WidgetController {

    @PostMapping(path = "/abnormal")
    public ResponseEntity<String> getabnormal() throws IOException {
        ClassPathResource abnormalResource = new ClassPathResource("dummys/widget/abnormaldummy.json");

        String abnormalContent = new String(Files.readAllBytes(Paths.get(abnormalResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(abnormalContent);
    }

    @PostMapping(path = "/cost")
    public ResponseEntity<String> getcost() throws IOException {
        ClassPathResource costResource = new ClassPathResource("dummys/widget/costdummy.json");

        String costContent = new String(Files.readAllBytes(Paths.get(costResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(costContent);
    }

    @PostMapping(path = "/cost_month_to_date")
    public ResponseEntity<String> getcostmonth() throws IOException {
        ClassPathResource costmonthResource = new ClassPathResource("dummys/widget/costmonthdummy.json");

        String costmonthContent = new String(Files.readAllBytes(Paths.get(costmonthResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(costmonthContent);
    }

    @PostMapping(path = "/estimated_cost")
    public ResponseEntity<String> getestimated() throws IOException {
        ClassPathResource estimatedResource = new ClassPathResource("dummys/widget/estimatedcostdummy.json");

        String estimatedContent = new String(Files.readAllBytes(Paths.get(estimatedResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(estimatedContent);
    }

    @PostMapping(path = "/product_portion")
    public ResponseEntity<String> getproduct() throws IOException {
        ClassPathResource productResource = new ClassPathResource("dummys/widget/productportiondummy.json");

        String productContent = new String(Files.readAllBytes(Paths.get(productResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productContent);
    }

    @PostMapping(path = "/top5")
    public ResponseEntity<String> gettop5() throws IOException {
        ClassPathResource top5Resource = new ClassPathResource("dummys/widget/top5dummy.json");

        String top5Content = new String(Files.readAllBytes(Paths.get(top5Resource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(top5Content);
    }

    @PostMapping(path = "/trend")
    public ResponseEntity<String> gettrend() throws IOException {
        ClassPathResource trendResource = new ClassPathResource("dummys/widget/trenddummy.json");

        String trendContent = new String(Files.readAllBytes(Paths.get(trendResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(trendContent);
    }
}
