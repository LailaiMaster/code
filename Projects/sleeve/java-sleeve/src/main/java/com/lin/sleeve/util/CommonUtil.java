package com.lin.sleeve.util;

import com.lin.sleeve.bo.PagerCounter;

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

}
