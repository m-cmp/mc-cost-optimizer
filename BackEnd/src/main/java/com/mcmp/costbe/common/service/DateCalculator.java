package com.mcmp.costbe.common.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
public class DateCalculator {

    public DateRangeModel dateRangeCalculator(String now) {
        String dateFormat = "yyyyMMdd";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalDate date = LocalDate.parse(now, formatter);

        YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
        LocalDateTime firstDayOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime lastDayOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        DateRangeModel result = new DateRangeModel();
        result.setStartDate(firstDayOfMonth);
        result.setEndDate(lastDayOfMonth);

        return result;
    }

    public String prevMonthdate(String mow){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(mow, formatter);
        LocalDate oneMonthBefore = date.minusMonths(1);

        return oneMonthBefore.format(formatter);
    }

}
