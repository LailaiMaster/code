package com.lin.sleeve.logic;

import com.lin.sleeve.bo.SkuOrderBO;
import com.lin.sleeve.core.enumeration.CouponType;
import com.lin.sleeve.core.money.IMoneyDiscount;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ForbiddenException;
import com.lin.sleeve.exception.http.ParameterException;
import com.lin.sleeve.model.Category;
import com.lin.sleeve.model.Coupon;
import com.lin.sleeve.util.CommonUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 15:06
 */
public class CouponChecker {

    private final Coupon coupon;
    private final IMoneyDiscount moneyDiscount;

    public CouponChecker(Coupon coupon, IMoneyDiscount moneyDiscount) {
        this.coupon = coupon;
        this.moneyDiscount = moneyDiscount;
    }

    /**
     * 优惠券是否在有效期
     */
    public void isOK() {
        boolean isInTimeline = CommonUtil.isInTimeLine(new Date(), coupon.getStartTime(), coupon.getEndTime());
        if (!isInTimeline) {
            throw new ForbiddenException(ExceptionCodes.C_40007);
        }
    }

    /**客户端的总价是否与服务端计算的总价一致*/
    public void finalTotalPriceIsOK(
            BigDecimal orderFinalTotalPrice,
            BigDecimal serverTotalPrice
    ) {
        BigDecimal serverFinalTotalPrice;

        switch (CouponType.toType(this.coupon.getType())) {
            case FULL_OFF: {
                serverFinalTotalPrice = this.moneyDiscount.discount(serverTotalPrice, coupon.getRate());
                break;
            }
            case FULL_MINUS: /*有门槛的*/
            case NO_THRESHOLD_MINUS: /*无门槛*/ {
                serverFinalTotalPrice = serverTotalPrice.subtract(this.coupon.getMinus());
                //不允许出现 <= 0 元的订单。
                if (serverFinalTotalPrice.compareTo(new BigDecimal("0")) <= 0) {
                    throw new ForbiddenException(ExceptionCodes.C_50008);
                }
                break;
            }
            default: {
                throw new ParameterException(ExceptionCodes.C_40009);
            }
        }

        /*客户端计算与服务端计算不一致*/
        if (serverFinalTotalPrice.compareTo(orderFinalTotalPrice) != 0) {
            throw new ParameterException(ExceptionCodes.C_50008);
        }
    }

    /**优惠券是否满足使用条件*/
    public void canBeUsed(List<SkuOrderBO> skuOrderBOList, BigDecimal serverTotalPrice) {
        BigDecimal orderCategoryPrice;
        if (this.coupon.getWholeStore()/*全程券，不需要计算分类的总价了*/) {
            orderCategoryPrice = serverTotalPrice;
        } else {
            List<Long> cidList = coupon.getCategoryList()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            //算出优惠券可用分类下，所有商品的总价。
            orderCategoryPrice = getSumByCategoryList(skuOrderBOList, cidList);
        }
        couponCanBeUsed(orderCategoryPrice);
    }

    private void couponCanBeUsed(BigDecimal orderCategoryPrice) {
        switch (CouponType.toType(coupon.getType())) {
            case FULL_OFF:
            case FULL_MINUS: {
                if (coupon.getFullMoney().compareTo(orderCategoryPrice) > 0) {
                    throw new ParameterException(ExceptionCodes.C_40008);
                }
                break;
            }
            case NO_THRESHOLD_MINUS: {
                break;
            }
            default: {
                throw new ParameterException(ExceptionCodes.C_40009);
            }
        }
    }

    private BigDecimal getSumByCategoryList(List<SkuOrderBO> skuOrderBOList, List<Long> cidList) {
        return cidList.stream()
                .map(cid -> this.getSumByCategory(skuOrderBOList, cid))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal("0"));
    }

    private BigDecimal getSumByCategory(List<SkuOrderBO> skuOrderBOList, Long cid) {
        return skuOrderBOList.stream()
                .filter(skuOrderBO -> skuOrderBO.getCategoryId().equals(cid))
                .map(SkuOrderBO::getTotalPrice)
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal("0"));
    }

}
