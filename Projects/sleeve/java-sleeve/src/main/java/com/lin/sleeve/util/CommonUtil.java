package com.lin.sleeve.util;

import com.lin.sleeve.bo.PagerCounter;

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

}
