package com.mcmp.dummybe.controller;

import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.model.login.CurrentLoginModel;
import com.mcmp.dummybe.service.login.LoginService;
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
@RequestMapping(path = "/api/v3/users")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping(path = "/current-logged-in")
    public ResponseEntity<ResultModel> getcurrentlogin() {
        ResultModel result = loginService.loginInfo();

        return ResponseEntity.ok().body(result);
    }

//    @PostMapping(path = "/current_login")
//    public ResponseEntity<String> getcurrentlogin() throws IOException {
//        ClassPathResource currentloginResource = new ClassPathResource("dummys/login/currentlogin.json");
//
//        String currentloginContent = new String(Files.readAllBytes(Paths.get(currentloginResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(currentloginContent);
//    }
}
