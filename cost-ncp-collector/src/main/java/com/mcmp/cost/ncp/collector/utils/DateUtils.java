package com.mcmp.cost.ncp.collector.utils;

import java.time.LocalDate;
import java.time.YearMonth;

public class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Cannot instantiate a utility class.");
    }


    /**
     * SystemZone 시간 기준의 yyyyMM 날짜(ex: 202509)를 구한다.
     * 1일 경우, 해당 달이 아닌, 전달을 계산해서(ex: 202508) 리턴한다.
     * @param today 현재 날짜
     * @return yyyyMM 날짜 포맷을 리턴
     */
    public static String getYearMonth(LocalDate today) {
        YearMonth targetMonth;
        if (today.getDayOfMonth() == 1) {
            targetMonth = YearMonth.from(today).minusMonths(1); // 전달
        } else {
            targetMonth = YearMonth.from(today); // 이번 달
        }
        // yyyyMM 문자열 만들기 (월을 항상 2자리로) ex) 202509
        return String.format("%04d%02d", targetMonth.getYear(), targetMonth.getMonthValue());
    }
}
