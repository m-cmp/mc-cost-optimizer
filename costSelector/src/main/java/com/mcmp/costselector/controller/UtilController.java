package com.mcmp.costselector.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/costopti/costslt")
public class UtilController {

    @GetMapping("/healthcheck")
    public ResponseEntity getHealthCheck(){
        try{
            return ResponseEntity.ok().body("OK");
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Alarm Service is not running");
        }
    }
}
