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
@RequestMapping(path = "/api/v3/billing")
public class BillingController {

    @PostMapping(path = "/aws/bills")
    public ResponseEntity<String> getbills() throws IOException {
        ClassPathResource billsResource = new ClassPathResource("dummys/billing/billsdummy.json");

        String billsContent = new String(Files.readAllBytes(Paths.get(billsResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(billsContent);
    }

    @PostMapping(path = "/aws/charge")
    public ResponseEntity<String> getcharges() throws IOException {
        ClassPathResource chargesResource = new ClassPathResource("dummys/billing/chargesdummy.json");

        String chargesContent = new String(Files.readAllBytes(Paths.get(chargesResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(chargesContent);
    }

    @PostMapping(path = "/aws/details")
    public ResponseEntity<String> getdetails() throws IOException {
        ClassPathResource detailsResource = new ClassPathResource("dummys/billing/detailsdummy.json");

        String detailsContent = new String(Files.readAllBytes(Paths.get(detailsResource.getURI())), StandardCharsets.UTF_8);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(detailsContent);
    }
}
