package com.lin.sleeve.vo;

import com.lin.sleeve.model.Coupon;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 1:20
 */
@Setter
@Getter
@ToString
public class CouponCategoryVO extends CouponPureVO{

    private final List<CategoryPureVO> categories = new ArrayList<>();

    public CouponCategoryVO(Coupon coupon) {
        super(coupon);
        coupon.getCategoryList().forEach(category -> categories.add(new CategoryPureVO(category)));
    }

}
