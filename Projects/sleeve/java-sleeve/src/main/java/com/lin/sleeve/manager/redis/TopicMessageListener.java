package com.lin.sleeve.manager.redis;

import com.lin.sleeve.bo.OrderMessageBO;
import com.lin.sleeve.service.CouponBackService;
import com.lin.sleeve.service.OrderCancelService;
import com.lin.sleeve.util.OrderUtil;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 22:01
 */
public class TopicMessageListener implements MessageListener {

    /*这里更好的方式还是使用发布订阅模式，不要让 TopicMessageListener 涉及到具体的业务。*/

    private final OrderCancelService orderCancelService;
    private final CouponBackService couponBackService;

    public TopicMessageListener(OrderCancelService orderCancelService, CouponBackService couponBackService) {
        this.orderCancelService = orderCancelService;
        this.couponBackService = couponBackService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        String expiredKey = new String(body);
        String topic = new String(channel);

        System.out.println("Redis.MessageListener.onMessage==============================>");
        System.out.println(expiredKey);
        System.out.println(topic);

        OrderMessageBO orderMessageBO = OrderUtil.getIdsFromRedisKey(expiredKey);

        //取消订单
        orderCancelService.cancel(orderMessageBO);
        //归还优惠券
        couponBackService.returnBack(orderMessageBO);
    }

}
