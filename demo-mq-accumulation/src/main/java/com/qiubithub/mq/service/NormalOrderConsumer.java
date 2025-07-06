package com.qiubithub.mq.service;

import com.qiubithub.mq.config.RocketMQConfig;
import com.qiubithub.mq.domain.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@com.qiubithub.mq.config.MQCondition
@RocketMQMessageListener(
    topic = RocketMQConfig.ORDER_TOPIC,
    consumerGroup = RocketMQConfig.ORDER_GROUP + "-normal",
    selectorExpression = RocketMQConfig.ORDER_TAG_NORMAL,
    consumeMode = ConsumeMode.CONCURRENTLY
)
public class NormalOrderConsumer implements RocketMQListener<OrderMessage> {

    @Override
    public void onMessage(OrderMessage message) {
        log.info("普通消费者接收到订单消息: {}", message);
        try {
            // 模拟处理时间，正常情况下处理较慢
            TimeUnit.MILLISECONDS.sleep(200);
            log.info("普通消费者处理订单消息完成: {}", message.getOrderId());
        } catch (Exception e) {
            log.error("处理订单消息异常", e);
        }
    }
}