package com.mcmp.collector;

import com.mcmp.collector.service.Cur;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.LocalDateTime;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class AwsTests {

    @Autowired
    private Cur service;

    @Test
    public void getS3List(){
        // service.batchInsertCURData("mcmpcostopti", LocalDateTime.now(), "1", "202408", null);
    }

}
