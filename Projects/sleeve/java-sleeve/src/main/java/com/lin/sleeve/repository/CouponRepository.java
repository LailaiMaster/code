package com.lin.sleeve.repository;

import com.lin.sleeve.model.Coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:51
 */
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /*
    通过分类 id 找出所有关联的优惠券。【这里需要去熟悉一个 SQL 的关联查询】

    原生sql，通过两次 join 来查询对于分类的优惠券【基本的多对多查询】：

        select * from coupon
        join coupon_category
        on coupon.id = coupon_category.coupon_id
        join category
        on coupon_category.category_id = category.id
        where category.id = ?

     等同于 JPQL：select c from Coupon c join c.categoryList ca where ca.id = :cid

     这里的 now 限制的是限制可以领取的优惠券，可以关联 Activity 的开启/结束时间。
     */
    @Query("select c from Coupon c  " +
            "join  c.categoryList ca  " +
            "join  Activity a on a.id = c.activityId  " +
            "where ca.id = :cid  " +
            "and a.startTime < :now  " +
            "and a.endTime > :now")
    List<Coupon> fndByCategory(@Param("cid") Long cid, @Param("now") Date now);

    @Query("select c from Coupon c  " +
            "join  Activity a on a.id = c.activityId  " +
            "where  c.wholeStore = :wholeStore " +
            "and a.startTime < :now  " +
            "and a.endTime > :now")
    List<Coupon> findByWholeStore(@Param("wholeStore") boolean wholeStore, @Param("now") Date now);

    @Query("select c from Coupon c\n" +
            "join UserCoupon uc\n" +
            "on c.id = uc.couponId\n" +
            "join User u\n" +
            "on u.id = uc.userId\n" +
            "where uc.status = 1\n" +
            "and u.id = :uid\n" +
            "and c.startTime < :now\n" +
            "and c.endTime > :now\n" +
            "and uc.orderId is null")
    List<Coupon> findMyAvailable(@Param("uid") Long uid, @Param("now") Date now);

    @Query("select c from Coupon c\n" +
            "join UserCoupon uc\n" +
            "on c.id = uc.couponId\n" +
            "join User u\n" +
            "on u.id = uc.userId\n" +
            "where u.id = :uid\n" +
            "and uc.status = 2\n" +
            "and uc.orderId is not null \n" +
            "and c.startTime < :now\n" +
            "and c.endTime > :now\n")
    List<Coupon> findMyUsed(@Param("uid") Long uid, @Param("now") Date now);

    @Query("select c from Coupon c\n" +
            "join UserCoupon uc\n" +
            "on c.id = uc.couponId\n" +
            "join User u\n" +
            "on u.id = uc.userId\n" +
            "where u.id = :uid\n" +
            "and uc.orderId is null \n" +
            "and uc.status <> 2\n" +
            "and c.endTime < :now\n")
    List<Coupon> findMyExpired(@Param("uid") Long uid, @Param("now") Date now);

}