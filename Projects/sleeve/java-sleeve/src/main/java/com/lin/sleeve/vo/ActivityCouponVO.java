package com.lin.sleeve.vo;

import com.lin.sleeve.model.Activity;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:27
 */
@Setter
@Getter
@ToString
public class ActivityCouponVO extends ActivityPureVO {

    private List<CouponPureVO> coupons;

    public ActivityCouponVO(Activity activity) {
        super(activity);
        this.coupons = activity.getCouponList()
                .stream()
                .map(CouponPureVO::new)
                .collect(Collectors.toList());
    }

}
