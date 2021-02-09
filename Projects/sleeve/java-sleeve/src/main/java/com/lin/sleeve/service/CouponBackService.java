package com.lin.sleeve.service;

import com.lin.sleeve.bo.OrderMessageBO;
import com.lin.sleeve.core.enumeration.OrderStatus;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ServerErrorException;
import com.lin.sleeve.model.Order;
import com.lin.sleeve.repository.OrderRepository;
import com.lin.sleeve.repository.UserCouponRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 22:12
 */
@Service
public class CouponBackService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Transactional
    public void returnBack(OrderMessageBO orderMessageBO) {
        Long couponId = orderMessageBO.getCouponId();
        Long uid = orderMessageBO.getUserId();

        if (couponId == -1) {
            return;
        }

        Order order = orderRepository.findById(orderMessageBO.getOrderId())
                .orElseThrow(() -> new ServerErrorException(ExceptionCodes.C_9999));

        if (order.getStatusEnum().equals(OrderStatus.UNPAID) || order.getStatusEnum().equals(OrderStatus.CANCELED)/*OrderCancelService 先执行*/) {
            this.userCouponRepository.returnBack(couponId, uid);
        }

    }

}
