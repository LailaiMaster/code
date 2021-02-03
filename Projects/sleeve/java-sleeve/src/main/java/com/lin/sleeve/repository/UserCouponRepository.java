package com.lin.sleeve.repository;

import com.lin.sleeve.model.UserCoupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/3 20:52
 */
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findFirstByUserIdAndCouponId(Long uid, Long couponId);

}
