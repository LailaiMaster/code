package com.lin.sleeve.util;

import com.lin.sleeve.bo.OrderMessageBO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/5 17:36
 */
@Component
public class OrderUtil {

    // A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z
    // A 代表 2021 年
    // B 代表 2022 年
    // 以此类推，这样做是为了减少数据库中字段的长度，从而利于查询性能。
    private static String[] yearCodes;

    public static String makeRedisKey(Long oid, Long uid, Long couponId) {
        if (couponId == null) {
            couponId = -1L;
        }
        return oid + "###" + uid + "###" + couponId;
    }

    public static OrderMessageBO getIdsFromRedisKey(String redisKey) {
        List<Long> collect = Stream.of(redisKey.split("###"))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return new OrderMessageBO(collect.get(0), collect.get(1), collect.get(2));
    }

    @Value("${sleeve.year-codes}")
    public void setYearCodes(String yearCodes) {
        OrderUtil.yearCodes = yearCodes.split(",");
    }

    // B3230651812529
    // 即使是精确到毫秒再加上一个随机数，也无法保证订单号 100% 的唯一，但是没有关系
    // 在存入数据库是会有防止重复机制，发现已存在的订单号就会拒绝提交订单
    // 因为这种事件发送的几率非常小，所以可以允许。
    public static String makeOrderNo() {
        StringBuilder joiner = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        String mills = String.valueOf(calendar.getTimeInMillis());
        String micro = LocalDateTime.now().toString();
        String random = String.valueOf(Math.random() * 1000).substring(0, 2);

        joiner.append(OrderUtil.yearCodes[calendar.get(Calendar.YEAR) - 2021])
                .append(Integer.toHexString(calendar.get(Calendar.MONTH) + 1).toUpperCase())
                .append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(mills.substring(mills.length() - 5))
                .append(micro.substring(micro.length() - 3))
                .append(random);

        return joiner.toString();
    }

}
