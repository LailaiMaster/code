package com.lin.sleeve.service;

import com.lin.sleeve.bo.OrderMessageBO;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ServerErrorException;
import com.lin.sleeve.model.Order;
import com.lin.sleeve.model.OrderSku;
import com.lin.sleeve.repository.OrderRepository;
import com.lin.sleeve.repository.SkuRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 22:12
 */
@Service
public class OrderCancelService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SkuRepository skuRepository;

    @Transactional
    public void cancel(OrderMessageBO orderMessageBO) {
        if (orderMessageBO.getOrderId() <= 0) {
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
        this.cancel(orderMessageBO.getOrderId());
    }

    /**todo：如果 redis 的消息，针对同一个 key 可能触发多次的话，那么下面逻辑是有问题的。*/
    private void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ServerErrorException(ExceptionCodes.C_9999));

        int res = orderRepository.cancelOrder(orderId);

        if (res != 1) {
            return;
        }

        //归还库存
        for (OrderSku snapItem : order.getSnapItems()) {
            skuRepository.recoverStock(snapItem.getId(), snapItem.getCount());
        }
    }

}
