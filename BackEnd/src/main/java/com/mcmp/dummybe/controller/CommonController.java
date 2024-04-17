package com.mcmp.dummybe.controller;

import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.service.common.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping(path = "/api/v3/common")
public class CommonController {
    @Autowired
    private CommonService commonService;


//    @PostMapping(path = "/companylist")
//    public ResponseEntity<String> getcompanylist() throws IOException {
//        ClassPathResource companylistResource = new ClassPathResource("dummys/common/companylistdummy.json");
//
//        String companylistContent = new String(Files.readAllBytes(Paths.get(companylistResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(companylistContent);
//    }
//
//    @PostMapping(path = "/noticelist")
//    public ResponseEntity<String> getnoticelist() throws IOException {
//        ClassPathResource noticelistResource = new ClassPathResource("dummys/common/noticelistdummy.json");
//
//        String noticelistContent = new String(Files.readAllBytes(Paths.get(noticelistResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(noticelistContent);
//    }
//
//    @PostMapping(path = "/noticescroll")
//    public ResponseEntity<String> getnoticescroll() throws IOException {
//        ClassPathResource noticescrollResource = new ClassPathResource("dummys/common/noticescrolldummy.json");
//
//        String noticescrollContent = new String(Files.readAllBytes(Paths.get(noticescrollResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(noticescrollContent);
//    }
//
//    @PostMapping(path = "/surveycheck")
//    public ResponseEntity<String> getsurveycheck() throws IOException {
//        ClassPathResource surveycheckResource = new ClassPathResource("dummys/common/surveycheckdummy.json");
//
//        String surveycheckContent = new String(Files.readAllBytes(Paths.get(surveycheckResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(surveycheckContent);
//    }

    @PostMapping(path = "/info")
    public ResponseEntity<ResultModel> getuserinfo(){
        ResultModel result = commonService.userInfo();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
//    @PostMapping(path = "/info")
//    public ResponseEntity<String> getuserinfo() throws IOException {
//        ClassPathResource userinfoResource = new ClassPathResource("dummys/common/userinfodummy.json");
//
//        String userinfoContent = new String(Files.readAllBytes(Paths.get(userinfoResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(userinfoContent);
//    }

    @GetMapping(path = "/cloud/vendor")
    public ResponseEntity<ResultModel> getvendor() {
        ResultModel result = new ResultModel();
        ResultModel resultData = commonService.vendorStatus();
        result.setData(resultData);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
//    @GetMapping(path = "/cloud/vendor")
//    public ResponseEntity<String> getvendor() throws IOException {
//        ClassPathResource vendorResource = new ClassPathResource("dummys/common/vendordummy.json");
//
//        String vendorContent = new String(Files.readAllBytes(Paths.get(vendorResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(vendorContent);
//    }

    @PostMapping(path = "/vendors")
    public ResponseEntity<ResultModel> getvendorlist() {
        ResultModel result = commonService.vendorList();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
//    @PostMapping(path = "/vendors")
//    public ResponseEntity<String> getvendorlist() throws IOException {
//        ClassPathResource vendorlistResource = new ClassPathResource("dummys/common/vendorlistdummy.json");
//
//        String vendorlistContent = new String(Files.readAllBytes(Paths.get(vendorlistResource.getURI())), StandardCharsets.UTF_8);
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(vendorlistContent);
//    }


    @PostMapping(path = "/authgroup/companyList")
    public ResponseEntity<ResultModel> getCompanyVendor(){
        ResultModel result = commonService.getCompanyVendor();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
}
