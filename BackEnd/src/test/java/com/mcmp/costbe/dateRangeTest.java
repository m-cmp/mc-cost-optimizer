package com.mcmp.costbe;

import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.resourceMapping.aws.AWSResourceMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class dateRangeTest {

    @Autowired
    private DateCalculator dateCalculator;


    @Test
    public void testCal(){
        List<LocalDateTime> result = dateCalculator.calculatePeriodDates("20240321", "30days");
//        System.out.println(result);
//        List<String> result = AWSResourceMapping.getData("Database");
        System.out.println(result);
    }

}
