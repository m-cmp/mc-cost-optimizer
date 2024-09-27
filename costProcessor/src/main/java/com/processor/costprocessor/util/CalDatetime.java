package com.processor.costprocessor.util;

import com.processor.costprocessor.model.util.CurrentDateModel;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalDatetime {
    public CurrentDateModel currentDatetime(){
        ZonedDateTime now = ZonedDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().plusDays(1).atStartOfDay();

        CurrentDateModel curDt = new CurrentDateModel();
        curDt.setCurStartDatetime(startOfToday);
        curDt.setCurEndDatetime(endOfToday);

        return curDt;
    }

    public CurrentDateModel monthDateRange(){
        ZonedDateTime now = ZonedDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().minusMonths(1).atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().atStartOfDay();

        CurrentDateModel curDt = new CurrentDateModel();
        curDt.setCurStartDatetime(startOfToday);
        curDt.setCurEndDatetime(endOfToday);

        return curDt;
    }


    public LocalDate getYesterDate(){
        return LocalDate.now().minusDays(1);
    }

    public List<LocalDate> getLastMonthWeeklyDates(){
        List<LocalDate> result = new ArrayList<>();

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate lastMonthSameDate = yesterday.minusMonths(1);
        DayOfWeek dayOfWeek = yesterday.getDayOfWeek();
        LocalDate lastMonthFirstDay = lastMonthSameDate.with(TemporalAdjusters.firstDayOfMonth());

        for(LocalDate date = lastMonthFirstDay;
            !date.isAfter(lastMonthSameDate.with(TemporalAdjusters.lastDayOfMonth()));
            date = date.plusDays(1)){
            if(date.getDayOfWeek() == dayOfWeek){
                result.add(date);
            }
        }

        return result;
    }
}
