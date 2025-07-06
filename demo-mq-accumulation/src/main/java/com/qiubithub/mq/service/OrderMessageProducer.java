package com.qiubithub.mq.service;

import com.qiubithub.mq.config.RocketMQConfig;
import com.qiubithub.mq.domain.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@com.qiubithub.mq.config.MQCondition
public class OrderMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 同步发送普通订单消息
     */
    public SendResult sendOrderMessage(OrderMessage orderMessage) {
        String destination = RocketMQConfig.ORDER_TOPIC + ":" + RocketMQConfig.ORDER_TAG_NORMAL;
        SendResult sendResult = rocketMQTemplate.syncSend(destination, 
                MessageBuilder.withPayload(orderMessage).build());
        log.info("同步发送普通订单消息结果: {}", sendResult);
        return sendResult;
    }

    /**
     * 异步发送普通订单消息
     */
    public void sendOrderMessageAsync(OrderMessage orderMessage) {
        String destination = RocketMQConfig.ORDER_TOPIC + ":" + RocketMQConfig.ORDER_TAG_NORMAL;
        rocketMQTemplate.asyncSend(destination, MessageBuilder.withPayload(orderMessage).build(), 
            new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("异步发送普通订单消息成功: {}", sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("异步发送普通订单消息失败", throwable);
                }
            });
    }

    /**
     * 发送优先级高的订单消息
     */
    public SendResult sendPriorityOrderMessage(OrderMessage orderMessage) {
        String destination = RocketMQConfig.ORDER_TOPIC + ":" + RocketMQConfig.ORDER_TAG_PRIORITY;
        SendResult sendResult = rocketMQTemplate.syncSend(destination, 
                MessageBuilder.withPayload(orderMessage).build());
        log.info("发送优先级订单消息结果: {}", sendResult);
        return sendResult;
    }
    
    /**
     * 批量生成订单消息，模拟消息堆积
     */
    public void generateBulkMessages(int count) {
        log.info("开始批量生成{}条订单消息", count);
        for (int i = 0; i < count; i++) {
            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setOrderId("BULK-" + System.currentTimeMillis() + "-" + i);
            orderMessage.setUserId("user-" + (i % 100));
            orderMessage.setStatus("CREATED");
            
            try {
                sendOrderMessage(orderMessage);
                // 稍微降低发送速率，避免过快发送
                if (i % 100 == 0) {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            } catch (Exception e) {
                log.error("批量发送消息失败", e);
            }
        }
        log.info("完成批量生成{}条订单消息", count);
    }
}