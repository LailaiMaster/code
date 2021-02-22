package com.lin.sleeve.manager.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Rocket 消息队列配置【项目中并没有实际使用】。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/9 14:57
 */
@Component
public class ConsumerSchedule implements CommandLineRunner {

    @Value("${rocketmq.consumer.producer-group}")
    private String consumerGroup;

    @Value("${rocketmq.namesrv-addr}")
    private String namesrvAddr;

    public void messageListener() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeMessageBatchMaxSize(1);

        consumer.subscribe("aaa", "*");

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            //在这里就可以去做订单取消。
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
    }

    /**作用类似于：@PostConstruct*/
    @Override
    public void run(String... args) throws Exception {
        //messageListener();
    }

}
