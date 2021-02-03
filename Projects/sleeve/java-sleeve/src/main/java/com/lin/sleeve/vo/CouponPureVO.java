package com.lin.sleeve.vo;

import com.lin.sleeve.model.Coupon;

import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:32
 */
@Setter
@Getter
@ToString
public class CouponPureVO {

    private Long id;
    private Long activityId;

    private Date startTime;
    private Date endTime;
    private Integer valitiy;//有效期

    private String title;
    private String description;

    private BigDecimal fullMoney;//满多少减
    private BigDecimal minus;//满减多少
    private BigDecimal rate;//打折
    private Boolean wholeStore;//是否全场券
    private Integer type;//类型

    private String remark;

    public CouponPureVO(Coupon coupon) {
        BeanUtils.copyProperties(coupon, this);
    }

    public static List<CouponPureVO> getList(List<Coupon> coupons) {
        return coupons.stream()
                .map(CouponPureVO::new)
                .collect(Collectors.toList());
    }

}