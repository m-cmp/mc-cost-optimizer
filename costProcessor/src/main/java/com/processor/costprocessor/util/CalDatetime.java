package com.processor.costprocessor.util;

import com.processor.costprocessor.model.util.CurrentDateModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class CalDatetime {
    public CurrentDateModel currentDatetime(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().plusDays(1).atStartOfDay();

        CurrentDateModel curDt = new CurrentDateModel();
        curDt.setCurStartDatetime(startOfToday);
        curDt.setCurEndDatetime(endOfToday);

        return curDt;
    }

    public CurrentDateModel monthDateRange(){
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        LocalDateTime startOfToday = now.toLocalDate().minusMonths(1).atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDateTime();

        CurrentDateModel curDt = new CurrentDateModel();
        curDt.setCurStartDatetime(startOfToday);
        curDt.setCurEndDatetime(endOfToday);

        return curDt;
    }
}
