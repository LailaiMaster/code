package com.ztiany.basic.print;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2019/11/29 9:18
 */
public class PrintPlans {

    /*
    | 日期|第一天学习|第二天复习|第四天复习|第七天复习|第十五天复习|
    |---|---|---|---|---|---|
     */

    public static void main(String... args) throws FileNotFoundException {
        List<OnePlan> onePlans = createPlan(getWeeOfToday(), 27, 163, 3);
        outputPlans(onePlans);
    }

    private static void outputPlans(List<OnePlan> onePlans) throws FileNotFoundException {
        File file = new File("files/plan.md");
        PrintWriter printWriter = new PrintWriter(file);

        printWriter.println("| 日期|第一天学习|第二天复习|第四天复习|第七天复习|第十五天复习|");
        printWriter.println("|---|---|---|---|---|---|");

        Calendar startDay = getWeeOfToday();
        Calendar endDay = (Calendar) onePlans.get(onePlans.size() - 1).startDate.clone();
        endDay.add(Calendar.DAY_OF_MONTH, REVIEW_15 - 1);

        while (startDay.getTimeInMillis() <= endDay.getTimeInMillis()) {
            printWriter.print("|");
            printWriter.print(SDF.format(startDay.getTime()));
            printWriter.print("|");
            printWriter.print(getDay(startDay, onePlans, REVIEW_1));
            printWriter.print("|");
            printWriter.print(getDay(startDay, onePlans, REVIEW_2));
            printWriter.print("|");
            printWriter.print(getDay(startDay, onePlans, REVIEW_4));
            printWriter.print("|");
            printWriter.print(getDay(startDay, onePlans, REVIEW_7));
            printWriter.print("|");
            printWriter.print(getDay(startDay, onePlans, REVIEW_15));
            printWriter.println();
            startDay.add(Calendar.DAY_OF_MONTH, 1);
        }

        printWriter.flush();
    }

    private static String getDay(Calendar startDay, List<OnePlan> onePlans, int offset) {
        for (OnePlan onePlan : onePlans) {
            if (onePlan.needStudy(startDay, offset)) {
                return String.format("第%d篇", onePlan.startNo);
            }
        }
        return "无";
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private static final int REVIEW_1 = 1;
    private static final int REVIEW_2 = 2;
    private static final int REVIEW_4 = 4;
    private static final int REVIEW_7 = 7;
    private static final int REVIEW_15 = 15;

    private static List<OnePlan> createPlan(Calendar startDay, int startNo, int endNo, int studyOffset) {
        List<OnePlan> plans = new ArrayList<>();
        for (int i = startNo; i <= endNo; i++) {
            plans.add(new OnePlan(i, startDay));
            startDay.add(Calendar.DAY_OF_MONTH, studyOffset);
        }
        return plans;
    }

    static class OnePlan {

        final int startNo;
        private final Calendar startDate;

        OnePlan(int startNo, Calendar startDate) {
            this.startNo = startNo;
            this.startDate = (Calendar) startDate.clone();
        }

        boolean needStudy(Calendar today, int days) {
            int offset = days - 1;
            startDate.add(Calendar.DAY_OF_MONTH, offset);
            long timeInMillis = startDate.getTimeInMillis();
            startDate.add(Calendar.DAY_OF_MONTH, -offset);
            return timeInMillis == today.getTimeInMillis();
        }

        @Override
        public String toString() {
            return "OnePlan{" +
                    "startNo=" + startNo +
                    ", startDate=" + SDF.format(startDate.getTime()) +
                    '}';
        }
    }

    private static Calendar getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

}