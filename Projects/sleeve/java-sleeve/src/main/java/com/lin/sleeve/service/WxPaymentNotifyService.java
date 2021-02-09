package com.lin.sleeve.service;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.lin.sleeve.core.enumeration.OrderStatus;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ServerErrorException;
import com.lin.sleeve.model.Order;
import com.lin.sleeve.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 16:05
 */
@Service
public class WxPaymentNotifyService {

    @Autowired
    private WxPaymentService wxPaymentService;

    @Autowired
    private OrderRepository orderRepository;

    public void processPayNotify(String data) {
        Map<String, String> dataMap;
        try {
            dataMap = WXPayUtil.xmlToMap(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }

        WXPay wxPay = this.wxPaymentService.assembleWxPayConfig();
        boolean valid;
        try {
            valid = wxPay.isResponseSignatureValid(dataMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }

        if (!valid) {
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }

        String returnCode = dataMap.get("return_code");
        String orderNo = dataMap.get("out_trade_no");
        String resultCode = dataMap.get("result_code");

        if (!returnCode.equals("SUCCESS")) {
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
        if (!resultCode.equals("SUCCESS")) {
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
        if (orderNo == null) {
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }

        this.deal(orderNo);
    }

    private void deal(String orderNo) {
        Order order = orderRepository.findFirstByOrderNo(orderNo).orElseThrow(() -> new ServerErrorException(ExceptionCodes.C_9999));
        // 未支付，则改为已支付。
        // 被取消，也应该改为已支付。【因为在 notify 的时候，可能延迟消息队列已经将订单改为了取消状态】
        if (order.getStatus().equals(OrderStatus.UNPAID.value()) || order.getStatus().equals(OrderStatus.CANCELED.value())) {
            int ret =  orderRepository.updateStatus(orderNo, OrderStatus.PAID.value());
            if (ret != 1) {
                throw new ServerErrorException(ExceptionCodes.C_9999);
            }
        }

    }

}