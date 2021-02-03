package com.lin.sleeve.service;


import com.lin.sleeve.core.enumeration.CouponStatus;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.exception.http.ParameterException;
import com.lin.sleeve.model.Activity;
import com.lin.sleeve.model.Coupon;
import com.lin.sleeve.model.UserCoupon;
import com.lin.sleeve.repository.ActivityRepository;
import com.lin.sleeve.repository.CouponRepository;
import com.lin.sleeve.repository.UserCouponRepository;
import com.lin.sleeve.util.CommonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:51
 */
@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    public List<Coupon> getByCategory(Long cid) {
        Date now = new Date();//优惠券不能过期
        return couponRepository.fndByCategory(cid, now);
    }

    public List<Coupon> getWholeStoreCoupons() {
        Date now = new Date();//优惠券不能过期
        return couponRepository.findByWholeStore(true, now);
    }

    public Optional<Coupon> findById(long id) {
        return couponRepository.findById(id);
    }

    public void collectOneCoupon(Long uid, long couponId) {
        //Does the coupon exist?
        couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException(ExceptionCodes.C_40003));

        //Does the activity exist?
        Activity activity = activityRepository.findByCouponListId(couponId)
                .orElseThrow(() -> new NotFoundException(ExceptionCodes.C_40010));

        //Is the activity expired?
        if (!CommonUtil.isInTimeLine(new Date(), activity.getStartTime(), activity.getEndTime())) {
            throw new ParameterException(ExceptionCodes.C_40005);
        }

        /*Is the coupon already collected by the user?*/
        userCouponRepository.findFirstByUserIdAndCouponId(uid, couponId).ifPresent(userCoupon -> {
            throw new ParameterException(ExceptionCodes.C_40006);
        });

        /*create and save.*/
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setCouponId(couponId);
        userCoupon.setUserId(uid);
        userCoupon.setStatus(CouponStatus.AVAILABLE.getValue());
        userCouponRepository.save(userCoupon);
    }

    public List<Coupon> getMyAvailableCoupons(Long uid) {
        return couponRepository.findMyAvailable(uid, new Date());
    }

    public List<Coupon> getMyExpiredCoupons(Long uid) {
        return couponRepository.findMyExpired(uid, new Date());
    }

    public List<Coupon> getMyUsedCoupons(Long uid) {
        return couponRepository.findMyUsed(uid, new Date());
    }

}
