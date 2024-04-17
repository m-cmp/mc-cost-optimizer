package com.mcmp.dummybe.controller;

import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.service.widget.WidgetService;
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
import java.text.ParseException;

@RestController
@RequestMapping(path = "/api/v3/dashboard")
public class WidgetController {
    @Autowired
    private WidgetService widgetService;

    @PostMapping(path = "/abnormal")
    public ResponseEntity<ResultModel> getabnormal() {
        ResultModel result = widgetService.makeAbnormal();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @PostMapping(path = "/cost")
    public ResponseEntity<ResultModel> getcost() throws ParseException {
        ResultModel result = widgetService.makeCostChart();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @PostMapping(path = "/cost_month_to_date")
    public ResponseEntity<ResultModel> getcostmonth() {
        ResultModel result = widgetService.makeCostMonth();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @PostMapping(path = "/estimated_cost")
    public ResponseEntity<ResultModel> getestimated() {
        ResultModel result = widgetService.makeEstimatedCost();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

//    @PostMapping(path = "/product_portion")
//    public ResponseEntity<String> getproduct() throws IOException {
//        ClassPathResource productResource = new ClassPathResource("dummys/widget/productportiondummy.json");
//        String productContent = new String(Files.readAllBytes(Paths.get(productResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productContent);
//
//    }
    @PostMapping(path = "/product_portion")
    public ResponseEntity<ResultModel> getproduct() throws IOException {
        ResultModel result = widgetService.makeProductPortionChart();

        return ResponseEntity.ok().body(result);
    }

//    @PostMapping(path = "/top5")
//    public ResponseEntity<String> gettop5() throws IOException {
//        ClassPathResource top5Resource = new ClassPathResource("dummys/widget/top5dummy.json");
//
//        String top5Content = new String(Files.readAllBytes(Paths.get(top5Resource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(top5Content);
//    }
    @PostMapping(path = "/top5")
    public ResponseEntity<ResultModel> gettop5() throws IOException {
        ResultModel result = widgetService.makeTop5Chart();

        return ResponseEntity.ok().body(result);
    }

    @PostMapping(path = "/trend")
    public ResponseEntity<ResultModel> gettrend() throws IOException {
//        ClassPathResource trendResource = new ClassPathResource("dummys/widget/trenddummy.json");
//        String trendContent = new String(Files.readAllBytes(Paths.get(trendResource.getURI())), StandardCharsets.UTF_8);
        ResultModel resultModel = widgetService.makeTrendChart();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resultModel);
    }
}
