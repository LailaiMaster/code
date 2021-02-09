package com.lin.sleeve.manager.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/9 14:32
 */
@Component
public class ProducerSchedule {

    private DefaultMQProducer producer;

    @Value("${rocketmq.producer.producer-group}")
    private String producerGroup;

    @Value("${rocketmq.namesrv-addr}")
    private String namesrvAddr;

    @PostConstruct
    public void defaultMQProducer() {
        this.producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        try {
            producer.start();
            System.out.println("RocketMQ starts successfully.");
        } catch (MQClientException e) {
            System.out.println("Starting RocketMQ failed.");
            e.printStackTrace();
        }
    }

    public String send(String topic, String messageText) throws Exception {
        Message message = new Message(topic, messageText.getBytes());
        //level：1  2    3            ...   9
        //time：1s 5s 10s          ...   30m
        message.setDelayTimeLevel(9);//level9--> 30 minutes
        SendResult sendResult = this.producer.send(message);
        System.out.println("rocketmq send result: " + sendResult.getMsgId());
        return sendResult.getMsgId();
    }

}
