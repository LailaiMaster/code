package com.lin.sleeve.repository;

import com.lin.sleeve.model.Activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:23
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findByName(String name);

    Optional<Activity> findByCouponListId(Long couponId);

}
