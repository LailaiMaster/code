package com.lin.sleeve.repository;

import com.lin.sleeve.model.UserCoupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/3 20:52
 */
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findFirstByUserIdAndCouponId(Long uid, Long couponId);

    Optional<UserCoupon> findFirstByUserIdAndCouponIdAndStatus(Long uid, Long couponId, int status);

    /*更细的时候还是要校验一次状态和订单号，防止优惠券被重复使用。*/
    @Query("update UserCoupon uc\n" +
            "set uc.status = 2, uc.orderId = :oid\n" +
            "where uc.userId = :uid\n" +
            "and uc.couponId = :cid\n" +
            "and uc.status = 1\n" +
            "and uc.orderId is null")
    int writeOffCoupon(@Param("cid") Long cid, @Param("uid") Long uid, @Param("oid") Long oid);

}
