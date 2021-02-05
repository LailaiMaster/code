package com.lin.sleeve.core.enumeration;

import java.util.stream.Stream;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/3 21:00
 */
public enum CouponStatus {

    AVAILABLE(1, "可用"),
    USED(2, "已使用"),
    EXPIRED(3, "过期");

    private Integer status;
    private String description;

    CouponStatus(Integer status, String description) {
        this.description = description;
        this.status = status;
    }

    public Integer getValue() {
        return status;
    }

    public static CouponStatus toType(int value) {
        return Stream.of(CouponStatus.values())
                .filter(couponStatus -> couponStatus.status == value)
                .findAny()
                .orElse(null);
    }

}
