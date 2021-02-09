package com.lin.sleeve.repository;

import com.lin.sleeve.model.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/5 20:29
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 找未支付的订单
     */
    Page<Order> findByExpiredTimeGreaterThanAndStatusAndUserId(Date now, Integer status, Long uid, Pageable pageable);

    Page<Order> findAllByUserId(Long uid, Pageable pageable);

    Page<Order> findAllByUserIdAndStatus(Long uid, Integer status, Pageable pageable);

    Optional<Order> findFirstByUserIdAndId(Long uid, Long oid);

    Optional<Order> findFirstByOrderNo(String orderNo);

    /**
     * 原始 sql，只有数据真的发送了变化，才会返回 1。而 JPQL 只要 Update 的条件匹配了，就会返回 1.
     */
    @Modifying
    @Query("update Order o set o.status = :status where o.orderNo = :orderNo")
    int updateStatus(String orderNo, int status);

    /**
     * 取消订单，必须要求订单的状态是未支付的。
     */
    @Modifying
    @Query("update Order o set o.status = 5 where  o.status = 1 and o.id = :oid")
    int cancelOrder(Long oid);

}
