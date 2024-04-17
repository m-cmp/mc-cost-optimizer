package com.mcmp.dummybe;

import com.mcmp.dummybe.dao.DashboardDAO;
import com.mcmp.dummybe.dao.login.LoginDAO;
import com.mcmp.dummybe.dao.common.CommonDAO;
import com.mcmp.dummybe.model.common.CompanyVendorModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class QueryTest {
    @Autowired
    private LoginDAO loginDAO;
    @Autowired
    private DashboardDAO dashboardDAO;
    @Autowired
    private CommonDAO commonDAO;

    @Test
    public void loginTest(){
        System.out.println(loginDAO.testQuery());
    }

    @Test
    public void dashboards(){
        System.out.println(dashboardDAO.selectWidgetAvailableOpt());
    }

    @Test
    public void companyTest() {
        List<CompanyVendorModel> test = commonDAO.vendorAccountList();
        System.out.println(test.toString());
    }
}
