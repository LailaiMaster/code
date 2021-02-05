package com.lin.sleeve.core.enumeration;

import java.util.stream.Stream;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 15:21
 */
public enum CouponType {

    FULL_MINUS(1, "满减"),
    FULL_OFF(2, "满折"),
    NO_THRESHOLD_MINUS(3, "无门槛");

    private Integer type;
    private String description;

    CouponType(Integer type, String description) {
        this.description = description;
        this.type = type;
    }

    public Integer getValue() {
        return type;
    }

    public static CouponType toType(int value) {
        return Stream.of(CouponType.values())
                .filter(couponType -> couponType.type == value)
                .findAny()
                .orElse(null);
    }

}
