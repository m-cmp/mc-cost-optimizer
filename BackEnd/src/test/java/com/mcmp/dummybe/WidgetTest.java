package com.mcmp.dummybe;


import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.service.widget.WidgetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class WidgetTest {

    @Autowired
    private WidgetService service;
    @Test
    public void trendTest(){
        ResultModel resultModel;
        resultModel= service.makeTrendChart();
        System.out.println(resultModel.toString());
    }
}
