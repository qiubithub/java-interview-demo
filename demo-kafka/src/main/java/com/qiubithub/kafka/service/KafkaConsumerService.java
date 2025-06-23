package com.qiubithub.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiubithub.kafka.model.Notification;
import com.qiubithub.kafka.model.Order;
import com.qiubithub.kafka.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 消费订单消息
     */
    @KafkaListener(topics = "${app.kafka.topics.order-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrder(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            Order order = objectMapper.readValue(message, Order.class);
            log.info("消费订单消息: {}, 分区: {}, 偏移量: {}", order.getOrderId(), partition, offset);
            
            // 处理订单逻辑
            processOrder(order);
            
            // 手动确认消息
            acknowledgment.acknowledge();
            
        } catch (JsonProcessingException e) {
            log.error("解析订单消息失败", e);
        } catch (Exception e) {
            log.error("处理订单消息异常", e);
            // 发生异常时不确认，消息会被重新消费
        }
    }

    /**
     * 消费用户消息
     */
    @KafkaListener(topics = "${app.kafka.topics.user-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUser(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            User user = objectMapper.readValue(message, User.class);
            log.info("消费用户消息: {}, 分区: {}, 偏移量: {}", user.getUserId(), partition, offset);
            
            // 处理用户逻辑
            processUser(user);
            
            // 手动确认消息
            acknowledgment.acknowledge();
            
        } catch (JsonProcessingException e) {
            log.error("解析用户消息失败", e);
        } catch (Exception e) {
            log.error("处理用户消息异常", e);
        }
    }

    /**
     * 消费通知消息
     */
    @KafkaListener(topics = "${app.kafka.topics.notification-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotification(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            Notification notification = objectMapper.readValue(message, Notification.class);
            log.info("消费通知消息: {}, 分区: {}, 偏移量: {}", notification.getNotificationId(), partition, offset);
            
            // 处理通知逻辑
            processNotification(notification);
            
            // 手动确认消息
            acknowledgment.acknowledge();
            
        } catch (JsonProcessingException e) {
            log.error("解析通知消息失败", e);
        } catch (Exception e) {
            log.error("处理通知消息异常", e);
        }
    }

    /**
     * 处理订单
     */
    private void processOrder(Order order) {
        // 模拟订单处理
        log.info("处理订单: {}, 状态: {}, 金额: {}", order.getOrderId(), order.getStatus(), order.getAmount());
        try {
            // 模拟处理时间
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 处理用户
     */
    private void processUser(User user) {
        // 模拟用户处理
        log.info("处理用户: {}, 用户名: {}, 邮箱: {}", user.getUserId(), user.getUsername(), user.getEmail());
        try {
            // 模拟处理时间
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 处理通知
     */
    private void processNotification(Notification notification) {
        // 模拟通知处理
        log.info("处理通知: {}, 标题: {}, 用户: {}", notification.getNotificationId(), notification.getTitle(), notification.getUserId());
        try {
            // 模拟处理时间
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}