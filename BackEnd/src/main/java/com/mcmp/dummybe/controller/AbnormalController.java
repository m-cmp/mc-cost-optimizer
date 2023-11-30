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
@RequestMapping(path = "/api/v3/anomaly")
public class AbnormalController {

    @PostMapping(path = "/detectedList")
    public ResponseEntity<String> getabnormal() throws IOException {
        ClassPathResource abnormalResource = new ClassPathResource("dummys/abnormal/abnormaldummy.json");

        String abnormalContent = new String(Files.readAllBytes(Paths.get(abnormalResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(abnormalContent);
    }
}
