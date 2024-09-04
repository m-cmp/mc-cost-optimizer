package com.mcmp.collector.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class DateCalculator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String getCurMonthRange(){
        LocalDate now = LocalDate.now(ZoneOffset.UTC);
        LocalDate firstDayofMonth = now.withDayOfMonth(1);
        LocalDate firstDayofNextMonth = firstDayofMonth.plusMonths(1);

        String start = firstDayofMonth.format(FORMATTER);
        String end = firstDayofNextMonth.format(FORMATTER);

        return start + "-" + end;
    }

    public String getMonthRange(String month, int term){
        LocalDate date = LocalDate.parse(month+"01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate firstDayofMonth = date.withDayOfMonth(1);
        LocalDate firstDayofNextMonth = firstDayofMonth.plusMonths(term);

        String start = firstDayofMonth.format(FORMATTER);
        String end = firstDayofNextMonth.format(FORMATTER);

        return start + "-" + end;
    }
}
