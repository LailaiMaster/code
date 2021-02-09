package com.lin.sleeve.util;

import com.lin.sleeve.bo.PagerCounter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 15:53
 */
public class CommonUtil {

    public static PagerCounter convertToPageParameter(Integer start, Integer count) {
        int pageNum = start / count;
        PagerCounter pagerCounter = new PagerCounter();
        pagerCounter.setCount(count);
        pagerCounter.setPage(pageNum);
        return pagerCounter;
    }

    public static boolean isInTimeLine(Date date, Date start, Date end) {
        long dateTime = date.getTime();
        long startTime = start.getTime();
        long endTime = end.getTime();
        return dateTime >= startTime && dateTime < endTime;
    }

    public static Calendar addSomeSeconds(Calendar calendar, int seconds) {
        calendar.add(Calendar.SECOND, seconds);
        return calendar;
    }

    public static Boolean isOutOfDate(Date start, Long period) {
        long now = Calendar.getInstance().getTimeInMillis();
        long startTimeStamp = start.getTime();
        long periodMillSecond = period * 1000;
        return now > (startTimeStamp + periodMillSecond);
    }

    public static Boolean isOutOfDate(Date expiredTime) {
        long now = Calendar.getInstance().getTimeInMillis();
        long expiredTimeStamp = expiredTime.getTime();
        return now > expiredTimeStamp;
    }

    public static String yuanToFenPlainString(BigDecimal value) {
        value = value.multiply(new BigDecimal("100"));
        return toPlain(value);
    }

    public static String toPlain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    public static String timestamp10() {
        long timestamp13 = Calendar.getInstance().getTimeInMillis();
        String timestamp13Str = Long.toString(timestamp13);
        return timestamp13Str.substring(0, timestamp13Str.length() - 3);
    }

}
