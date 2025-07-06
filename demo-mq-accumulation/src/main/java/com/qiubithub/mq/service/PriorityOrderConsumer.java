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
    consumerGroup = RocketMQConfig.ORDER_GROUP + "-priority",
    selectorExpression = RocketMQConfig.ORDER_TAG_PRIORITY,
    consumeMode = ConsumeMode.CONCURRENTLY
)
public class PriorityOrderConsumer implements RocketMQListener<OrderMessage> {

    @Override
    public void onMessage(OrderMessage message) {
        log.info("优先级消费者接收到订单消息: {}", message);
        try {
            // 优先级消息处理更快
            TimeUnit.MILLISECONDS.sleep(50);
            log.info("优先级消费者处理订单消息完成: {}", message.getOrderId());
        } catch (Exception e) {
            log.error("处理优先级订单消息异常", e);
        }
    }
}