package com.mcmp.collector;

import com.mcmp.collector.dao.AwsDao;
import com.mcmp.collector.properties.AlarmServiceUrlProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(AlarmServiceUrlProperties.class)
@RequiredArgsConstructor
public class CostCollectorApplication implements ApplicationRunner {

    private final AwsDao awsDao;

    public static void main(String[] args) {
        SpringApplication.run(CostCollectorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        try {
            awsDao.initTable(currentMonth);
            log.info("[CostCollector] tbl_table_billing_detail_{} ensured", currentMonth);
        } catch (Exception e) {
            log.warn("[CostCollector] Failed to init billing table for {}: {}", currentMonth, e.getMessage());
        }
    }
}
