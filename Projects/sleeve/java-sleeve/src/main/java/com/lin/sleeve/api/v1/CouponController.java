package com.lin.sleeve.api.v1;

import com.lin.sleeve.core.LocalUser;
import com.lin.sleeve.core.UnifyResponse;
import com.lin.sleeve.core.enumeration.CouponStatus;
import com.lin.sleeve.core.interceptors.ScopeLevel;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ParameterException;
import com.lin.sleeve.model.Coupon;
import com.lin.sleeve.service.CouponService;
import com.lin.sleeve.vo.CouponCategoryVO;
import com.lin.sleeve.vo.CouponPureVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:51
 */
@RestController/*@RestController = @Controller + @ResponseBody*/
@RequestMapping("/coupon")
@Validated
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 某个品类下可用的优惠券。
     */
    @GetMapping("/by/category/{cid}")
    public List<CouponPureVO> getCouponListByCategory(@PathVariable("cid") Long cid) {
        List<Coupon> coupons = couponService.getByCategory(cid);
        if (coupons.isEmpty()) {
            return Collections.emptyList();
        }
        return CouponPureVO.getList(coupons);
    }

    /**
     * 所有全场券。
     */
    @GetMapping("/whole_store")
    public List<CouponPureVO> getWholeStore() {
        List<Coupon> coupons = couponService.getWholeStoreCoupons();
        if (coupons.isEmpty()) {
            return Collections.emptyList();
        }
        return CouponPureVO.getList(coupons);
    }

    /**
     * 领取优惠券。
     */
    @ScopeLevel
    @PostMapping("/collect/{id}")
    public void collectCoupon(@PathVariable("id") Long id) {
        Long uid = LocalUser.getUser().getId();
        couponService.collectOneCoupon(uid, id);
        UnifyResponse.creatingSucceed(ExceptionCodes.C_OK);
    }

    /**
     * 用户的特定状态的优惠券列表。
     */
    @ScopeLevel
    @GetMapping("/myself/by/status/{status}")
    public List<CouponPureVO> getMyCouponByStatus(@PathVariable("status") Integer status) {
        Long uid = LocalUser.getUser().getId();
        List<Coupon> couponList;
        switch (CouponStatus.toType(status)) {
            case AVAILABLE: {
                couponList = couponService.getMyAvailableCoupons(uid);
                break;
            }
            case USED: {
                couponList = couponService.getMyUsedCoupons(uid);
                break;
            }
            case EXPIRED: {
                couponList = couponService.getMyExpiredCoupons(uid);
                break;
            }
            default: {
                throw new ParameterException(ExceptionCodes.C_40001);
            }
        }
        return CouponPureVO.getList(couponList);
    }

    /**
     * 用户的所有优惠券列表。
     */
    @ScopeLevel()
    @GetMapping("/myself/available/with_category")
    public List<CouponCategoryVO> getUserCouponWithCategory() {
        Long uid = LocalUser.getUser().getId();
        List<Coupon> coupons = couponService.getMyAvailableCoupons(uid);
        if (coupons.isEmpty()) {
            return Collections.emptyList();
        }
        return coupons.stream().map(CouponCategoryVO::new).collect(Collectors.toList());
    }

}
