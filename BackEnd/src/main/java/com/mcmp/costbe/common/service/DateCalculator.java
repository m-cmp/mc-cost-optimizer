package com.mcmp.costbe.common.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    public String prevMonthdate(String now){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(now, formatter);
        LocalDate oneMonthBefore = date.minusMonths(1);

        return oneMonthBefore.format(formatter);
    }


    public String curMonthDate(String now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(now, formatter);

        return date.format(formatter);
    }
  
    public List<LocalDateTime> calculatePeriodDates(String Date, String periodType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate specificDate = LocalDate.parse(Date, formatter);
        List<LocalDateTime> dateTimes = new ArrayList<>();
        LocalTime time = LocalTime.MIDNIGHT;

        switch (periodType) {
            case "7days":
                for (int i = 7; i >= 1; i--) {
                    dateTimes.add(specificDate.minusDays(i).atTime(time));
                }
                break;
            case "3months":
                for (int i = 6; i >= 0; i--) {
                    dateTimes.add(specificDate.minusDays(15 * i).atTime(time));
                }
                break;
            case "30days":
            default:
                dateTimes.add(specificDate.minusDays(30).atTime(time));
                dateTimes.add(specificDate.minusDays(26).atTime(time));
                dateTimes.add(specificDate.minusDays(22).atTime(time));
                dateTimes.add(specificDate.minusDays(18).atTime(time));
                dateTimes.add(specificDate.minusDays(15).atTime(time));
                dateTimes.add(specificDate.minusDays(10).atTime(time));
                dateTimes.add(specificDate.minusDays(4).atTime(time));
                break;
        }

        return dateTimes;
    }

    public List<String> LocalDateTimeToString(List<LocalDateTime> localDateTimeList){
        List<String> dateList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LocalDateTime dateTime : localDateTimeList) {
            dateList.add(dateTime.toLocalDate().format(formatter));
        }
        return dateList;
    }

    public LocalDateTime curUTCLocalDateTime(){
        return ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
    }

    public LocalDate curUTCLocalDate(){
        return ZonedDateTime.now(ZoneId.of("UTC")).toLocalDate();
    }

    public LocalDate curLocalDate(){
        return ZonedDateTime.now().toLocalDate();
    }


}
